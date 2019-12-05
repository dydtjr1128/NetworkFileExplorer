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

public class AsyncFileExplorer {
    private final AsynchronousChannelGroup channelGroup;
    private final AsynchronousSocketChannel asc;
    private ProtocolManager protocolManager;

    public AsyncFileExplorer(String ip, int port) throws IOException {
        protocolManager = new ProtocolManager();
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

    private AsynchronousSocketChannel initClientSocketChannel() throws IOException {
        return AsynchronousSocketChannel.open(channelGroup);
    }


    private void startRead() {
        ByteBuffer input = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
        readDataFromServer(input);
    }

    private void readDataFromServer(ByteBuffer input) {
        if (!asc.isOpen())
            return;
        int len = 0;
        asc.read(input, len, new CompletionHandler<Integer, Integer>() {

            @Override
            public void completed(Integer result, Integer length) {
                if (result < 1) {
                    try {
                        asc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Socket closed!");
                } else {
                    if (length == 0) {
                        input.flip();
                        length = (int) input.getLong();
                        input.compact();
                    }
                    if (length + 1 <= input.position()) {
                        input.flip();
                        BindingData bindingData = null;
                        try {
                            bindingData = ProtocolConverter.convertData(input, length);
                            protocolManager.executeProtocol(asc, bindingData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        input.clear();
                        length = 0;
                    }
                    asc.read(input, length, this);
                }
            }

            @Override
            public void failed(Throwable exc, Integer buffer) {
                System.out.println("Connection closed from server");
            }

        });
    }
}
