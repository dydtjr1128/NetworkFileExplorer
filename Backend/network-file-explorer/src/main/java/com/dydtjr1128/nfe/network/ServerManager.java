package com.dydtjr1128.nfe.network;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ServerManager {
    public void startServer() throws IOException {
        new Thread(new AsyncServer()).start();
    }
}
