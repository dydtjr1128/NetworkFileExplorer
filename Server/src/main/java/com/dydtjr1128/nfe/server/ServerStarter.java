package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.server.config.StartOrder;
import com.dydtjr1128.nfe.server.fileServer.AsyncFileServer;
import com.dydtjr1128.nfe.server.messageServer.AsyncServer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(StartOrder.SERVER_STARTER)
public class ServerStarter {
    public void startServer() throws IOException {
        new Thread(new AsyncServer()).start();
        new Thread(new AsyncFileServer()).start();
    }
}
