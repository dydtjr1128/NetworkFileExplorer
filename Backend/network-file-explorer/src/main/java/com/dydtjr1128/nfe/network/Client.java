package com.dydtjr1128.nfe.network;

import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private AsynchronousSocketChannel socketChannel;
    private ClientDataReceiver receiver;
    private InetSocketAddress inetSocketAddress;
    private static boolean isWriting = false;
    private final static Queue<ByteBuffer> messageQueue = new LinkedList<>();
    private BlockingQueue<BindingData> resultBlockingQueue;
    Client(AsynchronousSocketChannel channel, ClientDataReceiver receiver) throws IOException {
        this.socketChannel = channel;
        this.receiver = receiver;
        if (channel.isOpen()) {
            inetSocketAddress = ((InetSocketAddress) socketChannel.getRemoteAddress());
        }
        resultBlockingQueue = new LinkedBlockingDeque<>();
    }
    public BlockingQueue<BindingData> getBlockingQueue(){
        return resultBlockingQueue;
    }

    private void sendMessageToClient(final ByteBuffer buffer) {
        synchronized (messageQueue) {
            messageQueue.add(buffer);
            if (isWriting) {
                //System.out.println("쓰기 불가능");
                return;
            }
        }
        loadAndWriteMessage();
    }

    private void loadAndWriteMessage() {
        ByteBuffer byteBuffer;
        synchronized (messageQueue) {
            byteBuffer = messageQueue.poll();
            isWriting = byteBuffer != null;
        }
        if (isWriting) {
            //System.out.println("가능");
            writeData(byteBuffer);
        } else {
            //System.out.println("불가능");
        }
    }

    private void writeData(ByteBuffer buffer) {
        //buffer.flip();
        System.out.println("@@" + new String(buffer.array()));
        socketChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer resultBuffer) {
                if (buffer.hasRemaining()) {
                    System.out.println("@@@" + StandardCharsets.UTF_8.decode(resultBuffer).toString());
                    socketChannel.write(buffer, resultBuffer, this);
                } else {
                    System.out.println("@@@!" + StandardCharsets.UTF_8.decode(resultBuffer).toString());
                    loadAndWriteMessage();
                    // Go back and check if there is new data to write
                }

            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }

    public void writeStringMessage(byte protocol, String msg){
        try {
            ByteBuffer byteBuffer = NFEProtocol.makeTransferData(protocol, msg);
            byteBuffer.flip();
            sendMessageToClient(byteBuffer/*ByteBuffer.wrap(string.getBytes())*/);
        } catch (IOException e){
            e.getStackTrace();
        }
    }

    public String getClientURL() {

        return inetSocketAddress.getAddress().toString() + "/" + inetSocketAddress.getPort();

    }
    public String getClientIP() {

        return inetSocketAddress.getAddress().toString().substring(1);

    }

    public void run() {
        receiver.receive(this);
    }

    public void read(CompletionHandler<Integer, ByteBuffer> completionHandler) {
        ByteBuffer input = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
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

    /* requset function */

    public void getDirectoriesByPath(String path){
        writeStringMessage(NFEProtocol.GET_LIST, path);
    }
    public void removeDirectoriesByPath(String path){
        writeStringMessage(NFEProtocol.DELETE, path);
    }
    public void changeFileName(String payload) { writeStringMessage(NFEProtocol.CHANGE_NAME, payload);}
}
