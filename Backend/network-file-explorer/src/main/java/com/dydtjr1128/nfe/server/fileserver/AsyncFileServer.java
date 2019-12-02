package com.dydtjr1128.nfe.server.fileserver;

import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import com.dydtjr1128.nfe.server.AdminWebSocketManager;
import com.dydtjr1128.nfe.server.AsyncServer;
import com.dydtjr1128.nfe.server.Client;
import com.dydtjr1128.nfe.server.ClientManager;
import com.dydtjr1128.nfe.server.config.Config;
import com.dydtjr1128.nfe.server.model.AdminMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Queue;
import java.util.concurrent.*;

public class AsyncFileServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AsyncServer.class);
    private final AsynchronousServerSocketChannel assc;
    private final AsynchronousChannelGroup channelGroup;

    public AsyncFileServer() throws IOException {
        channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Config.FILE_THREAD_POOL_COUNT, Executors.defaultThreadFactory());
        assc = createAsynchronousFileServerSocketChannel();
        logger.debug("[Finish server setting with " + Config.DEFAULT_THREAD_POOL_COUNT + " thread in thread pool]");
    }

    private AsynchronousServerSocketChannel createAsynchronousFileServerSocketChannel() throws IOException {
        final AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocketChannel.bind(new InetSocketAddress(Config.ASYNC_FILE_SERVER_PORT));
        return serverSocketChannel;
    }

    @Override
    public void run() {
        assc.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                logger.debug("[Accept new file connection]");

                if (assc.isOpen())
                    assc.accept(null, this);

                try {
                    handleNewConnection(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failed(Throwable exc, Void attachment) {
            }
        });
    }

    private void handleNewConnection(AsynchronousSocketChannel channel) throws IOException {
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        ByteBuffer dataBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_FILE_BYTE);
        Future<Integer> operations = channel.read(dataBuffer);
        while (!operations.isDone()) {
        }
        dataBuffer.flip();
        byte action = dataBuffer.get();
        dataBuffer.clear();
        if (action == FileAction.FILE_RECEIVE_FROM_CLIENT) { // 고정 서버 path + filename
            System.out.println("@@@act1");
            readFileFromClient(channel, dataBuffer);
        } else if (action == FileAction.FILE_SEND_TO_CLIENT) { // PATH + fileanme
            System.out.println("@@@act2");
            Client client = ClientManager.getInstance().clientsHashMap.get(((InetSocketAddress) channel.getRemoteAddress()).getAddress().toString());
            TransferFileMetaData metaData;
            synchronized (client.getFilePathQueue()) {
                Queue<TransferFileMetaData> queue = client.getFilePathQueue();
                metaData = queue.poll();
            }
            if (metaData != null)
                writeFileToClient(channel, dataBuffer, metaData);
        }
    }

    public void readFileFromClient(AsynchronousSocketChannel channel, ByteBuffer dataBuffer22) {
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(NFEProtocol.NETWORK_FILE_BYTE);
        channel.read(dataBuffer, new Attachment(), new CompletionHandler<Integer, Attachment>() {

            @Override
            public void completed(final Integer result, final Attachment readData) {
                if (result > 0) {
                    dataBuffer.flip();
                    //System.out.println(dataBuffer.position() + " @@@ " + dataBuffer.limit());
                    long messageLen = dataBuffer.getLong();
                    byte[] bytes = new byte[(int) messageLen];
                    dataBuffer.get(bytes);
                    String string = null;
                    try {
                        System.out.println(new String(bytes));
                        string = Snappy.uncompressString(bytes);
                        if (string.contains(Config.END_MESSAGE_MARKER)) {
                            readData.calcFileData(string);
                            Path path = Paths.get(Config.FILE_STORE_PATH + readData.getFileName());
                            if (Files.notExists(Paths.get(Config.FILE_STORE_PATH))) {
                                try {
                                    Files.createDirectories(Paths.get(Config.FILE_STORE_PATH));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (Files.notExists(path) && !Files.isDirectory(path)) {

                                System.out.println(path);
                                readData.openFileChannel(path);

                                dataBuffer.clear();
                                writeToFile(channel, readData, dataBuffer);
                            } else {
                                close(channel, readData.getFileChannel());
                                System.out.println("Download err!");
                                return;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        close(channel, readData.getFileChannel());
                    }
                }

            }

            @Override
            public void failed(final Throwable exc, final Attachment attachment) {
                close(channel, null);
                System.out.println("file client exit");
            }
        });
    }

    public void writeToFile(AsynchronousSocketChannel channel, Attachment attachment, ByteBuffer dataBuffer) {
        System.out.println(attachment.getReadPosition() + " " + dataBuffer.position() + " " + dataBuffer.limit());
        channel.read(dataBuffer, attachment, new CompletionHandler<Integer, Attachment>() {

                    @Override
                    public void completed(Integer result, Attachment attachment) {
                        if (result > 0) {
                            dataBuffer.flip();
                            try {
                                Future<Integer> operation = attachment.getFileChannel().write(dataBuffer, attachment.getReadPosition());
                                operation.get(10, TimeUnit.SECONDS);
                            } catch (Exception e) {
                                System.out.println("timeout");
                                e.printStackTrace();
                            }
                            attachment.addPosition(result);
                            if (attachment.getReadPosition() == attachment.getFileSize()) {
                                AdminWebSocketManager.getInstance().writeToAdminPage(new AdminMessage(AdminMessage.DOWNLOAD_SUCCESS, attachment.getFileName() + " 다운로드 성공!"));
                                System.out.println(attachment.getFileName() + "@@@@@ 끝!!");
                                try {
                                    channel.close();
                                    attachment.getFileChannel().close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return;
                            } else if(attachment.getReadPosition() > attachment.getFileSize()){
                                AdminWebSocketManager.getInstance().writeToAdminPage(new AdminMessage(AdminMessage.DOWNLOAD_FAIL, attachment.getFileName() + " 다운로드 실패!"));
                                System.out.println("size err");
                                return;
                            }
                            dataBuffer.clear();
                            channel.read(dataBuffer, attachment, this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Attachment attachment) {
                        AdminWebSocketManager.getInstance().writeToAdminPage(new AdminMessage(AdminMessage.DOWNLOAD_FAIL, attachment.getFileName() + " 다운로드 실패!"));
                    }
                }
        );

    }


    public void writeFileToClient(AsynchronousSocketChannel channel, ByteBuffer dataBuffer, TransferFileMetaData metaData) throws IOException {
        Path serverPath = Paths.get(metaData.getSeverPath());

        long position = 0;
        if (Files.exists(serverPath) && !Files.isDirectory(serverPath)) {
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                    serverPath,
                    StandardOpenOption.READ
            );
            long fileSize = Files.size(serverPath);
            final String message = metaData.getClientPath() + Config.MESSAGE_DELIMITTER + fileSize + Config.END_MESSAGE_MARKER;
            dataBuffer.put(message.getBytes());
            dataBuffer.flip();
            while (dataBuffer.hasRemaining()) {
                channel.write(dataBuffer);
            }
            dataBuffer.clear();
            fileChannel.read(
                    dataBuffer, 0, position,    // null 대신 iterations 전달
                    new CompletionHandler<Integer, Long>() {

                        @Override
                        public void completed(Integer result, Long readByte) {
                            if (result == -1) {
                                System.err.println("비정상 종료");
                                return;
                            }
                            readByte += result;
                            dataBuffer.flip();
                            Future<Integer> future = channel.write(dataBuffer);
                            try {
                                future.get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            dataBuffer.clear();
                            if (readByte == fileSize) {
                                System.err.println("AsynchronousFileChannel.read() 완료");

                                return;
                            }

                            fileChannel.read(dataBuffer, readByte, readByte, this);
                        }

                        @Override
                        public void failed(Throwable exc, Long attachment) {
                            try {
                                channel.close();
                                fileChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

    public void close(AsynchronousSocketChannel channel, AsynchronousFileChannel fileChannel) {
        try {
            if (fileChannel != null && fileChannel.isOpen())
                fileChannel.close();
            if (!channel.isOpen())
                channel.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FileServer close err!");
        }
    }

}
