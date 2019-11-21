package network;

import protocol.BindingData;
import protocol.NFEProtocol;
import protocol.ProtocolConverter;
import protocol.ProtocolManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncFileExplorer implements Runnable {
    private static final String SEVER_IP = "192.168.190.103";
    private static final int SEVER_PORT = 14411;
    private final AsynchronousSocketChannel asc;
    private AtomicInteger sendAtomicInteger = new AtomicInteger(0);
    private AtomicInteger readAtomicInteger = new AtomicInteger(0);
    private final static Queue<ByteBuffer> messageQueue = new LinkedList<ByteBuffer>();
    private boolean isWriting = false;

    public AsyncFileExplorer() throws IOException, ExecutionException, InterruptedException {

        asc = initClientSocketChannel();
        asc.connect(new InetSocketAddress(SEVER_IP, SEVER_PORT), asc, new CompletionHandler<Void, AsynchronousSocketChannel>() {
            @Override
            public void completed(Void result, AsynchronousSocketChannel channel) {
                startRead();
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                System.out.println("err!");
            }
        });
    }

    public AsynchronousSocketChannel initClientSocketChannel() throws IOException {
        return AsynchronousSocketChannel.open();
    }

    @Override
    public void run() {
       /* String msg = "hello" + sendAtomicInteger;
        try {
            ByteBuffer byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.GET_LIST, msg);
            sendAtomicInteger.getAndIncrement();
            sendMessageToClient(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        while (true) {
            String msg = "hello" + sendAtomicInteger;
            ByteBuffer buffer = null;
            try {
                buffer = NFEProtocol.makeTransferData(NFEProtocol.GET_LIST, msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendAtomicInteger.getAndIncrement();
            sendMessageToClient(buffer);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startRead() {
        ByteBuffer input = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
        readDataFromServer(input);
    }

    private void readDataFromServer(ByteBuffer input) {
        if (!asc.isOpen())
            return;
        asc.read(input, input, new CompletionHandler<Integer, ByteBuffer>() {

            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                System.out.print("result : " + result + " " + buffer.position() + " ");
                if (result < 1) {
                    System.out.println("remain?");
                } else {
                    buffer.flip();
                    //message is read from server
                    readAtomicInteger.getAndIncrement();

                    //String message = StandardCharsets.UTF_8.decode(buffer).toString();
                    BindingData bindingData = null;
                    try {
                        bindingData = ProtocolConverter.convertData(buffer);
                        ProtocolManager.getInstance().executeProtocol(asc, bindingData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    buffer.clear();
                    asc.read(buffer, buffer, this);
                    //print the message
                    //System.out.println("Read message:" + " " + message + " " + message.length() + " @@@@" + readAtomicInteger);

                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                System.out.println("fail to read message from server");
            }

        });
    }

    private void sendMessageToClient(final ByteBuffer buffer) {
        synchronized (messageQueue) {
            messageQueue.add(buffer);
            if (isWriting) {
                //System.out.println("쓰기 불가능");
                return;
            }
        }
        writeMessage();
    }

    private void writeMessage() {
        ByteBuffer buffer;
        synchronized (messageQueue) {
            buffer = messageQueue.poll();
            isWriting = buffer != null;
        }
        if (isWriting) {
            //System.out.println("가능");
            startWrite(asc, buffer);
        } else {
            //System.out.println("불가능");
        }

    }

    private void startWrite(final AsynchronousSocketChannel sockChannel, final ByteBuffer buffer) {
        sockChannel.write(buffer, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                if (buffer.hasRemaining()) {
                    sockChannel.write(buffer, sockChannel, this);
                    /*System.out.println("@@@" + String.valueOf(buffer.get()));*/
                } else {
                    writeMessage();
                    // Go back and check if there is new data to write
                }
                System.out.println("message send!");
                //NOTHING TO DO
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("Fail to write the message to server");
            }
        });
    }
}
