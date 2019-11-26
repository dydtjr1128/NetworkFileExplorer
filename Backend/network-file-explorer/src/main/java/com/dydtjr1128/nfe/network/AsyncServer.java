package com.dydtjr1128.nfe.network;

import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;

public class AsyncServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AsyncServer.class);
    private static int port, threadPoolCount;
    private final AsynchronousServerSocketChannel assc;
    private final AsynchronousChannelGroup channelGroup;

    public AsyncServer() throws IOException {
        initializeServerConfig();

        channelGroup = AsynchronousChannelGroup.withFixedThreadPool(threadPoolCount, Executors.defaultThreadFactory());
        assc = createAsynchronousServerSocketChannel();
        logger.debug("[Finish server setting with " + threadPoolCount + " thread in thread pool]");
    }

    public SocketAddress getSocketAddress() throws IOException {
        return assc.getLocalAddress();
    }

    @Override
    public void run() {
        logger.debug("[New client waiting...]");
        assc.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                // 비동기 소켓 연결 // accept the next connection
                if (assc.isOpen())
                    assc.accept(null, this);
                try {
                    handleNewConnection(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
            }
        });
    }

    private void initializeServerConfig() throws IOException {
        port = 14411;
        threadPoolCount = Runtime.getRuntime().availableProcessors();
    }


    private AsynchronousServerSocketChannel createAsynchronousServerSocketChannel() throws IOException {
        final AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocketChannel.bind(new InetSocketAddress(port));
        return serverSocketChannel;
    }

    private void handleNewConnection(AsynchronousSocketChannel channel) throws IOException {
        Client client = new Client(channel, new ClientDataReceiver(this, new DefaultDataHandler()));
        logger.debug("[New client connected] : " + client.getClientIP());
        try {
            channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        } catch (IOException e) {
            // ignore
        }
        ClientManager.getInstance().addClient(client);
        client.run();
    }

    public void writeMessageToClients(String clientIP, String message) throws IOException {
        logger.debug("[Send message to client] : " + clientIP);
        ClientManager.getInstance().clientsHashMap.get(clientIP).writeStringMessage(NFEProtocol.GET_LIST, "C:\\Windows\\Cursors");
    }
}
