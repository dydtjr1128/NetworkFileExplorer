package com.dydtjr1128.nfe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ClientDataReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ClientDataReceiver.class);
    private AsyncServer server;
    private ClientDataHandler handler;

    public ClientDataReceiver(AsyncServer server, ClientDataHandler handler) {
        this.server = server;
        this.handler = handler;
    }

    public void receive(final Client client) {
        System.out.println("@@connected : " + ClientManager.getInstance().getClientCount());
        client.read(new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                // if result is negative or zero the connection has been closed or something gone wrong
                if (result < 1) {
                    client.close();
                    logger.debug("[Closing connection to ] : " + client);
                    ClientManager.getInstance().removeClient(client);
                } else {
                    logger.debug("[Some data received from ] : " + client.getClientIP());
                    handler.onDataReceive(client, buffer, result);
                    // enqueue next round of actions
                    client.run();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                System.out.println("1");
                client.close();
                System.out.println("2");
                ClientManager.getInstance().removeClient(client);
                System.out.println("3");

            }
        });
    }
}
