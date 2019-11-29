package com.dydtjr1128.nfe.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
        client.read(new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                // 음수나 0이면 연결 종료
                if (result < 1) {
                    client.close();
                    logger.debug("[Closing connection to ] : " + client);
                    ClientManager.getInstance().removeClient(client);
                } else {
                    logger.debug("[Some data received from] : " + client.getClientIP());
                    try {
                        handler.onDataReceive(client, buffer, result);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
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
