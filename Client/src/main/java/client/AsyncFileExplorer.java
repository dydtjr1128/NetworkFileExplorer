package client;

import protocol.core.BindingData;
import protocol.core.NFEProtocol;
import protocol.core.ProtocolConverter;
import protocol.core.ProtocolManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncFileExplorer implements Runnable {
    private final AsynchronousChannelGroup channelGroup;
    private final AsynchronousSocketChannel asc;
    private AtomicInteger readAtomicInteger = new AtomicInteger(0);
    public AsyncFileExplorer(String ip, int port) throws IOException {
        channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory()
        );
        asc = initClientSocketChannel();
        asc.connect(new InetSocketAddress(ip, port), asc, new CompletionHandler<Void, AsynchronousSocketChannel>() {
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
        return AsynchronousSocketChannel.open(channelGroup);
    }

    @Override
    public void run() {
        /*String msg = "hello" + sendAtomicInteger;
        try {
            ByteBuffer byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.GET_LIST, msg);
            sendAtomicInteger.getAndIncrement();
            sendMessageToServer(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
                System.out.println("result : " + result + " " + buffer.position() + " ");
                if (result < 1) {
                    System.out.println("remain?");
                } else {
                    buffer.flip();
                    //message is read from server

                    BindingData bindingData = null;
                    try {
                        bindingData = ProtocolConverter.convertData(buffer);
                        ProtocolManager.getInstance().executeProtocol(asc, bindingData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    buffer.clear();
                    asc.read(buffer, buffer, this);

                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                System.out.println("Connection closed from server");
            }

        });
    }
}
