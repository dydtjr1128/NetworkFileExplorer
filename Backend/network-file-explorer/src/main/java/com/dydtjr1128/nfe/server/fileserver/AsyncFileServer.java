package com.dydtjr1128.nfe.server.fileserver;

import com.dydtjr1128.nfe.admin.service.ApplicationContextProvider;
import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import com.dydtjr1128.nfe.server.AdminWebSocketManager;
import com.dydtjr1128.nfe.server.AsyncServer;
import com.dydtjr1128.nfe.server.Client;
import com.dydtjr1128.nfe.server.ClientManager;
import com.dydtjr1128.nfe.server.config.Config;
import com.dydtjr1128.nfe.server.model.AdminMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AsyncFileServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AsyncServer.class);
    private final AsynchronousServerSocketChannel assc;
    private final AsynchronousChannelGroup channelGroup;
    private ClientManager clientManager;
    private AdminWebSocketManager adminWebSocketManager;

    public AsyncFileServer() throws IOException {
        channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Config.FILE_THREAD_POOL_COUNT, Executors.defaultThreadFactory());
        assc = createAsynchronousFileServerSocketChannel();
        clientManager = ApplicationContextProvider.getApplicationContext().getBean(ClientManager.class);
        adminWebSocketManager = ApplicationContextProvider.getApplicationContext().getBean(AdminWebSocketManager.class);
        logger.debug("[Finish server setting with " + Config.DEFAULT_THREAD_POOL_COUNT + " thread in thread pool]");
    }

    private AsynchronousServerSocketChannel createAsynchronousFileServerSocketChannel() throws IOException {
        final AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, NFEProtocol.NETWORK_FILE_BYTE * 2);
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
                } catch (IOException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failed(Throwable exc, Void attachment) {
            }
        });
    }

    private void handleNewConnection(AsynchronousSocketChannel channel) throws IOException, ExecutionException, InterruptedException {
        //channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.setOption(StandardSocketOptions.SO_RCVBUF, NFEProtocol.NETWORK_FILE_BYTE);
        channel.setOption(StandardSocketOptions.SO_SNDBUF, NFEProtocol.NETWORK_FILE_BYTE);
        ByteBuffer dataBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_FILE_BYTE);
        Future<Integer> operations = channel.read(dataBuffer);
        operations.get();
        dataBuffer.flip();
        byte action = dataBuffer.get();

        if (dataBuffer.hasRemaining())
            dataBuffer.compact();
        else
            dataBuffer.clear();
        //추가
        if (action == FileAction.FILE_RECEIVE_FROM_CLIENT) { // 고정 서버 path + filename
            logger.debug("[File receive from client]");
            readFileFromClient(channel, dataBuffer);
        } else if (action == FileAction.FILE_SEND_TO_CLIENT) { // PATH + fileanme
            logger.debug("[File send to client]");
            Client client = clientManager.getClientByIP((InetSocketAddress) channel.getRemoteAddress());
            TransferFileMetaData metaData;
            if (client == null) return;
            synchronized (client.getFilePathQueue()) {
                Queue<TransferFileMetaData> queue = client.getFilePathQueue();
                metaData = queue.poll();
            }
            if (metaData != null)
                writeFileToClient(channel, dataBuffer, metaData);
        }
    }

    private void readFileFromClient(AsynchronousSocketChannel channel, ByteBuffer dataBuffer) {
        channel.read(dataBuffer, new Attachment(), new CompletionHandler<Integer, Attachment>() {

            @Override
            public void completed(final Integer result, final Attachment readData) {
                if (result < 0) {
                    logger.error("Read data error!");
                    close(channel, readData.getFileChannel());
                    return;
                }
                dataBuffer.flip();
                long messageLen = dataBuffer.getLong();

                byte[] bytes = new byte[(int) messageLen];
                dataBuffer.get(bytes, 0, (int) messageLen);

                if (dataBuffer.hasRemaining())
                    dataBuffer.compact();
                else
                    dataBuffer.clear();

                String string = null;
                try {
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
                            logger.debug("[Receive file from server] : " + path);
                            readData.openFileChannel(path);

                            writeToFile(channel, readData, dataBuffer);
                        } else {
                            close(channel, readData.getFileChannel());
                        }
                    }
                } catch (IOException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    close(channel, readData.getFileChannel());
                }
            }

            @Override
            public void failed(final Throwable exc, final Attachment attachment) {
                close(channel, null);
                logger.debug("[Error!]" + exc.toString());
            }
        });
    }

    private void writeToFile(AsynchronousSocketChannel channel, Attachment attachment, ByteBuffer dataBuffer) throws ExecutionException, InterruptedException {
        dataBuffer.flip();
        while (dataBuffer.hasRemaining()) {
            Future<Integer> future = attachment.getFileChannel().write(dataBuffer, attachment.getReadPosition());
            int l = future.get();
            attachment.addPosition(l != -1 ? l : 0);
        }
        dataBuffer.clear();
        channel.read(dataBuffer, attachment, new CompletionHandler<Integer, Attachment>() {

                    @Override
                    public void completed(Integer result, Attachment attachment) {
                        if (result > 0) {
                            dataBuffer.flip();
                            try {
                                while (dataBuffer.hasRemaining()) {
                                    Future<Integer> future = attachment.getFileChannel().write(dataBuffer, attachment.getReadPosition());
                                    int i = future.get(10, TimeUnit.SECONDS);
                                    attachment.addPosition(i);
                                }
                            } catch (Exception e) {
                                logger.error("[Download error!] : " + attachment.getFileName(), e);
                                return;
                            }

                            dataBuffer.clear();
                            if (attachment.getReadPosition() == attachment.getFileSize()) {
                                adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.REQUEST_FAIL, attachment.getFileName() + " 다운로드 성공!"));
                                logger.debug("[Download success!] : " + attachment.getFileName());
                                try {
                                    channel.close();
                                    attachment.getFileChannel().close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return;
                            } else if (attachment.getReadPosition() > attachment.getFileSize()) {
                                adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.REQUEST_FAIL, attachment.getFileName() + " 다운로드 실패!"));
                                logger.debug("[Download Error!]");
                            }
                            channel.read(dataBuffer, attachment, this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Attachment attachment) {
                        adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.REQUEST_FAIL, attachment.getFileName() + " 다운로드 실패!"));
                        logger.debug("[Error!]" + exc.toString());
                    }
                }
        );

    }


    private void writeFileToClient(AsynchronousSocketChannel channel, ByteBuffer dataBuffer, TransferFileMetaData metaData) throws IOException, ExecutionException, InterruptedException {
        Path serverPath = Paths.get(metaData.getSeverPath());
        logger.debug("[Send file to Server] : " + serverPath);
        if (Files.exists(serverPath) && !Files.isDirectory(serverPath)) {
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                    serverPath,
                    StandardOpenOption.READ
            );
            long fileSize = Files.size(serverPath);
            byte[] message = Snappy.compress(serverPath.getFileName().toString() + Config.MESSAGE_DELIMITER + fileSize + Config.END_MESSAGE_MARKER);

            dataBuffer.putLong(message.length);
            dataBuffer.put(message);
            dataBuffer.flip();
            Future<Integer> future = channel.write(dataBuffer);
            future.get();
            dataBuffer.clear();

            fileChannel.read(
                    dataBuffer, 0, new SendData(0, dataBuffer),    // null 대신 iterations 전달
                    new CompletionHandler<Integer, SendData>() {

                        @Override
                        public void completed(Integer result, SendData sendData) {
                            if (result < 0) {
                                logger.error("File read error");
                                return;
                            }
                            sendData.addPosition(result);
                            try {
                                dataBuffer.flip();
                                while (dataBuffer.hasRemaining()) {
                                    Future<Integer> future = channel.write(dataBuffer);
                                    future.get(100, TimeUnit.SECONDS);
                                }

                            } catch (Exception e) {
                                adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.REQUEST_FAIL, serverPath + " 업로드 실패!"));
                                e.printStackTrace();
                                return;
                            }

                            if (sendData.getReadPosition() == fileSize) {
                                logger.debug("[Upload success!] : " + metaData.getSeverPath());
                                adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.REQUEST_SUCCESS, serverPath + " 업로드 성공!"));
                                return;
                            }
                            sendData.getBuffer().clear();
                            fileChannel.read(sendData.getBuffer(), sendData.getReadPosition(), sendData, this);
                        }

                        @Override
                        public void failed(Throwable exc, SendData attachment) {
                            try {
                                channel.close();
                                fileChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.REQUEST_FAIL, serverPath + " 업로드 실패!"));
                        }
                    });
        }
    }

    private void close(AsynchronousSocketChannel channel, AsynchronousFileChannel fileChannel) {
        try {
            if (fileChannel != null && fileChannel.isOpen())
                fileChannel.close();
            if (!channel.isOpen())
                channel.close();
        } catch (IOException e) {
            logger.error("Filer channel close error!", e);
        }
    }

}
