package file.sender;

import config.Config;
import file.FileAction;
import org.xerial.snappy.Snappy;
import protocol.core.NFEProtocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FileSender {
    private final String ip;
    private final int port;

    public FileSender(final String ip, final int port) {
        this.ip = ip;
        this.port = port;
    }

    public void send(final String path) {
        new Thread(() -> {
            AsynchronousSocketChannel channel = null;
            try {
                channel = AsynchronousSocketChannel.open();
                channel.setOption(StandardSocketOptions.SO_RCVBUF, NFEProtocol.NETWORK_FILE_BYTE);
                channel.setOption(StandardSocketOptions.SO_SNDBUF, NFEProtocol.NETWORK_FILE_BYTE);
                channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteBuffer dataBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_FILE_BYTE);
            channel.connect(new InetSocketAddress(ip, port), channel, new CompletionHandler<Void, AsynchronousSocketChannel>() {
                @Override
                public void completed(Void result, AsynchronousSocketChannel channel) {
                    try {
                        dataBuffer.clear();
                        dataBuffer.put(FileAction.FILE_RECEIVE_FROM_CLIENT);
                        dataBuffer.flip();
                        try {
                            while (dataBuffer.hasRemaining()) {
                                Future<Integer> future = channel.write(dataBuffer);
                                future.get();
                            }
                            dataBuffer.clear();
                            readFromFile(channel, dataBuffer, path);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                    System.out.println("err!" + exc.toString());
                    exc.printStackTrace();
                }
            });
        }).start();
    }

    private void readFromFile(AsynchronousSocketChannel channel, ByteBuffer dataBuffer, String path) throws IOException, ExecutionException, InterruptedException {
        Path clientPath = Paths.get(path);
        System.out.println("[Send file to Server] : " + clientPath);
        if (Files.exists(clientPath) && !Files.isDirectory(clientPath)) {
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                    Paths.get(path),
                    StandardOpenOption.READ
            );
            long fileSize = Files.size(clientPath);
            byte[] message = Snappy.compress(clientPath.getFileName().toString() + Config.MESSAGE_DELIMITTER + fileSize + Config.END_MESSAGE_MARKER);
            dataBuffer.putLong(message.length);
            dataBuffer.put(message);
            dataBuffer.flip();
            while (dataBuffer.hasRemaining()) {
                Future<Integer> future = channel.write(dataBuffer);
                future.get();
            }
            dataBuffer.clear();

            fileChannel.read(
                    dataBuffer, 0, new SendData(0, dataBuffer),    // null 대신 iterations 전달
                    new CompletionHandler<Integer, SendData>() {

                        @Override
                        public void completed(Integer result, SendData sendData) {
                            if (result < 0) {
                                System.err.println("비정상 종료");
                                return;
                            }
                            sendData.addPosition(result);
                            try {
                                dataBuffer.flip();
                                while (dataBuffer.hasRemaining()) {
                                    Future<Integer> future = channel.write(dataBuffer);
                                    future.get();
                                }
                                dataBuffer.clear();

                            } catch (Exception e) {
                                System.out.println("timeout");
                                e.printStackTrace();
                                return;
                            }

                            if (sendData.getReadPosition() == fileSize) {
                                dataBuffer.clear();
                                try {
                                    fileChannel.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }

                            fileChannel.read(dataBuffer, sendData.getReadPosition(), sendData, this);
                        }

                        @Override
                        public void failed(Throwable exc, SendData attachment) {
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

}
