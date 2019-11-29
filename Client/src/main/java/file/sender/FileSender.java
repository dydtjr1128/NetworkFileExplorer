package file.sender;

import config.Config;
import file.FileAction;
import protocol.core.NFEProtocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
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

   /* void send(final String path) throws IOException {
        SocketChannel channel = SocketChannel.open(new InetSocketAddress(ip, port));
        FileChannel fileChannel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
        final ByteBuffer dataBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
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
                position += fileChannel.transferTo(position, NFEProtocol.NETWORK_BYTE, channel);//Send data using DMA
            }
            System.out.println("전송 완료!");
        }
        channel.close();
        fileChannel.close();
    }*/

    public void send(final String path) throws IOException {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        final ByteBuffer dataBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
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

    }

    private void readFromFile(AsynchronousSocketChannel channel, ByteBuffer dataBuffer, String path) throws IOException {
        Path clientPath = Paths.get(path);

        long position = 0;
        System.out.println(Files.exists(clientPath) + " @ " + Files.notExists(clientPath) + "@ " + !Files.isDirectory(clientPath) + "@ "+clientPath);
        if (Files.exists(clientPath) && !Files.isDirectory(clientPath)) {
            System.out.println("11");
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
                    Paths.get(path),
                    StandardOpenOption.READ
            );
            long fileSize = Files.size(clientPath);
            final String message = clientPath.getFileName().toString() + Config.MESSAGE_DELIMITTER + fileSize + Config.END_MESSAGE_MARKER;
            System.out.println(message+"!!!!");
            dataBuffer.put(StandardCharsets.UTF_8.encode(message));
            dataBuffer.flip();
            System.out.println("22");
            while (dataBuffer.hasRemaining()) {
                channel.write(dataBuffer);
                System.out.println("33");
            }
            dataBuffer.clear();
            fileChannel.read(
                    dataBuffer, 0, position,    // null 대신 iterations 전달
                    new CompletionHandler<Integer, Long>() {

                        @Override
                        public void completed(Integer result, Long readByte) {
                            System.out.println("44");
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

                            if (readByte == fileSize) {
                                System.err.println("AsynchronousFileChannel.read() 완료");
                                try {
                                    channel.close();
                                    fileChannel.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                            dataBuffer.clear();
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

}
