package com.dydtjr1128.nfe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private AsynchronousSocketChannel socketChannel;
    private ClientDataReceiver receiver;
    private InetSocketAddress inetSocketAddress;

    Client(AsynchronousSocketChannel channel, ClientDataReceiver receiver) throws IOException {
        this.socketChannel = channel;
        this.receiver = receiver;
        if (channel.isOpen()) {
            inetSocketAddress = ((InetSocketAddress) socketChannel.getRemoteAddress());
        }
    }

    private void sendMessageToClient(ByteBuffer buffer) {
        socketChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    socketChannel.write(buffer, buffer, this);
                } else {
                    // Go back and check if there is new data to write
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }

    public void writeStringMessage(String string) {
        sendMessageToClient(ByteBuffer.wrap(string.getBytes()));
    }

    public String getClientIP() {

        return inetSocketAddress.getAddress().toString() + "/" + inetSocketAddress.getPort();

    }

    public void run() {
        receiver.receive(this);
    }

    public void read(CompletionHandler<Integer, ByteBuffer> completionHandler) {
        ByteBuffer input = ByteBuffer.allocate(256);
        if (!socketChannel.isOpen()) {
            return;
        }
        socketChannel.read(input, input, completionHandler);
    }

    public void close() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
