package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.server.fileserver.AsyncFileServer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ServerStarter {
    public void startServer() throws IOException {
        new Thread(new AsyncServer()).start();
        new Thread(new AsyncFileServer()).start();
    }
}
