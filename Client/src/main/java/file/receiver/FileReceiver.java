package file.receiver;

import config.Config;
import file.FileAction;
import org.xerial.snappy.Snappy;
import protocol.core.NFEProtocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FileReceiver {
    private final String ip;
    private final int port;
    private long st;

    public FileReceiver(final String ip, final int port) {
        this.ip = ip;
        this.port = port;
    }

    public void receive(final String storePath) throws IOException {
        st = System.currentTimeMillis();
        final ByteBuffer dataBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_FILE_BYTE);
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        channel.connect(new InetSocketAddress(ip, port), channel, new CompletionHandler<Void, AsynchronousSocketChannel>() {
            @Override
            public void completed(Void result, AsynchronousSocketChannel channel) {
                dataBuffer.clear();
                dataBuffer.put(FileAction.FILE_SEND_TO_CLIENT);
                dataBuffer.flip();
                while (dataBuffer.hasRemaining()){
                    Future<Integer> future = channel.write(dataBuffer);
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                dataBuffer.clear();
                readFileFromServer(channel, dataBuffer, storePath);
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                System.out.println("err!");
                exc.printStackTrace();
            }
        });

    }

    private void readFileFromServer(AsynchronousSocketChannel channel, ByteBuffer dataBuffer, String storePath) {
        channel.read(dataBuffer, new Attachment(), new CompletionHandler<Integer, Attachment>() {

            @Override
            public void completed(final Integer result, final Attachment readData) {
                if (result > 0) {
                    dataBuffer.flip();
                    long messageLen = dataBuffer.getLong();
                    byte[] bytes = new byte[(int) messageLen];
                    dataBuffer.get(bytes, 0, (int)messageLen);

                    if (dataBuffer.hasRemaining())
                        dataBuffer.compact();
                    else
                        dataBuffer.clear();

                    String string = null;
                    try {
                        string = Snappy.uncompressString(bytes);
                        if (string.contains(Config.END_MESSAGE_MARKER)) {
                            readData.calcFileData(string);
                            Path path = Paths.get(storePath + "/" + readData.getFileName());
                            if (Files.notExists(Paths.get(storePath))) {
                                try {
                                    Files.createDirectories(Paths.get(storePath));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (Files.notExists(path) && !Files.isDirectory(path)) {
                                System.out.println("[Receive file from server] : " + path);
                                readData.openFileChannel(path);

                                writeToFile(channel, readData, dataBuffer);
                            } else {
                                close(channel, readData.getFileChannel());
                                System.out.println("Download err! File is exist.");
                            }
                        }
                    } catch (IOException | InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        close(channel, readData.getFileChannel());
                    }
                }

            }

            @Override
            public void failed(final Throwable exc, final Attachment attachment) {
                close(channel, null);
                System.out.println("fail!");
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
                                while (dataBuffer.hasRemaining()){
                                    Future<Integer> future = attachment.getFileChannel().write(dataBuffer, attachment.getReadPosition());
                                    int i = future.get(10, TimeUnit.SECONDS);
                                    attachment.addPosition(i);
                                }
                            } catch (Exception e) {
                                System.out.println("[Download error!] : " + attachment.getFileName());
                                return;
                            }

                            dataBuffer.clear();
                            if (attachment.getReadPosition() == attachment.getFileSize()) {
                                System.out.println("[Download success!] : " + attachment.getFileName());
                                System.out.println(System.currentTimeMillis()-st + "ms");
                                try {
                                    channel.close();
                                    attachment.getFileChannel().close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return;
                            } else if (attachment.getReadPosition() > attachment.getFileSize()) {
                                System.out.println("err!");
                                return;
                            }
                            channel.read(dataBuffer, attachment, this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Attachment attachment) {
                        System.out.println("[Error!]" + exc.toString());
                    }
                }
        );
    }

    private void close(AsynchronousSocketChannel channel, AsynchronousFileChannel fileChannel) {
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
