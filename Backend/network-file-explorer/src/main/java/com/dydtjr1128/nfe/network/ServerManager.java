package com.dydtjr1128.nfe.network;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ServerManager {
    public ServerManager(){

    }
    public void startServer() throws IOException {
        /*ClientAccepter clientAccepter = new ClientAccepter();
        ClientProcessor clientProcessor = new ClientProcessor();

        clientAccepter.start();
        clientProcessor.start();*/
        new Thread(new AsyncServer()).start();
    }
}
