


/*
package com.dydtjr1128.nfe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

public class ClientProcessor extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClientProcessor.class);
    private BlockingQueue<SocketChannel> channelBlockingQueue;
    private Selector readSelector;
    private ByteBuffer readBuffer;
    private Charset charset;

    public ClientProcessor() {
        try {
            readSelector = Selector.open();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        this.channelBlockingQueue = ClientManager.getInstance().socketChannelBlockingQueue;
        charset = StandardCharsets.UTF_8;

        readBuffer = ByteBuffer.allocate(1024);
    }

    public void run() {
        while (true) {
            try {

                logger.debug("[tt]");
                processSocketChannelQueue();

                int numKeys = readSelector.select();
                if (numKeys > 0) {
                    readData();
                }
            } catch (IOException | InterruptedException e) {
                //
            }
        }
    }

    private void processSocketChannelQueue() throws IOException, InterruptedException {
        SocketChannel socketChannel;
        do {
            socketChannel = channelBlockingQueue.take();
            logger.debug("[socketChannel]");
            socketChannel.configureBlocking(false);
            socketChannel.register(readSelector, SelectionKey.OP_READ);
        } while (!channelBlockingQueue.isEmpty());
    }

    private void readData() {
        Iterator iter = readSelector.selectedKeys().iterator();
        while (iter.hasNext()) {
            SelectionKey key = (SelectionKey) iter.next();
            iter.remove();

            SocketChannel socketChannel = (SocketChannel) key.channel();
            try {
                int byteCount = socketChannel.read(readBuffer);
                if (byteCount == -1) {
                    throw new IOException();
                }
                logger.debug("[요청 처리: " + socketChannel.getRemoteAddress() + ": " + Thread.currentThread().getName()
                        + "]");

                readBuffer.flip();
                String result = charset.decode(readBuffer).toString();
                logger.debug("[Message: " + result + "]");
                writeData(result, socketChannel);
            } catch (IOException e) {
                // 에러 발생
            }
        }
    }

    private void writeData(String requestData, SocketChannel socketChannel) throws IOException {
        ByteBuffer writeBuffer = charset.encode(requestData);
        socketChannel.write(writeBuffer);
    }
}*/
