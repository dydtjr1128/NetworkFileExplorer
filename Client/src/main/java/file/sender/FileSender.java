package file.sender;

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
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FileSender {
    private final String ip;
    private final int port;

    public FileSender(final String ip, final int port) {
        this.ip = ip;
        this.port = port;
    }

   /* void send(final String path) throws IOException {
        SocketChannel channel = SocketChannel.open(new InetSocketAddress(ip, port));
        FileChannel fileChannel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
        final ByteBuffer dataBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_FILE_BYTE);
        final File srcFile = new File(path);
        if (srcFile.exists() && !srcFile.isDirectory()) {
            final String message = srcFile.getName() + Config.MESSAGE_DELIMITTER + srcFile.length() + Config.END_MESSAGE_MARKER;
            dataBuffer.put(message.getBytes());
            dataBuffer.flip();
            while (dataBuffer.hasRemaining()) {
                channel.write(dataBuffer);
            }
            long position = 0;
            while (position < srcFile.length()) {
                position += fileChannel.transferTo(position, NFEProtocol.NETWORK_FILE_BYTE, channel);//Send data using DMA
            }
            System.out.println("전송 완료!");
        }
        channel.close();
        fileChannel.close();
    }*/

    public void send(final String path) {
        System.out.println(path + "@ start!!");
        new Thread(() -> {
            AsynchronousSocketChannel channel = null;
            try {
                channel = AsynchronousSocketChannel.open();
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
                        Future<Integer> future = channel.write(dataBuffer);
                        try {
                            future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        dataBuffer.clear();
                        readFromFile(channel, dataBuffer, path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                    System.out.println("err!");
                }
            });
        }).start();
    }

    private void readFromFile(AsynchronousSocketChannel channel, ByteBuffer dataBuffer, String path) throws IOException {
        Path clientPath = Paths.get(path);
        if (Files.exists(clientPath) && !Files.isDirectory(clientPath)) {
            System.out.println("11");
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                    Paths.get(path),
                    StandardOpenOption.READ
            );
            long fileSize = Files.size(clientPath);
            byte[] message = Snappy.compress(clientPath.getFileName().toString() + Config.MESSAGE_DELIMITTER + fileSize + Config.END_MESSAGE_MARKER);

            dataBuffer.putLong(message.length);
            dataBuffer.put(message);
            dataBuffer.flip();
            channel.write(dataBuffer);
            dataBuffer.clear();
            dataBuffer.limit(0);
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
                                sendData.getBuffer().flip();
                                //System.out.println(sendData.getReadPosition() + " " + sendData.getBuffer().position() + " " + sendData.getBuffer().limit());
                                Future<Integer> operation = channel.write(sendData.getBuffer());
                                operation.get(100, TimeUnit.SECONDS);
                                Thread.sleep(10);
                            } catch (Exception e) {
                                System.out.println("timeout");
                                e.printStackTrace();
                                return;
                            }

                            if (sendData.getReadPosition() == fileSize) {
                                System.err.println(sendData.getReadPosition() + "AsynchronousFileChannel.read() 완료");
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
                        }
                    });
            System.in.read();
        }


    }

}
