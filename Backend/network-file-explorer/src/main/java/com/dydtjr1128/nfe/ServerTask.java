package com.dydtjr1128.nfe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ServerTask implements Callable<Socket> {
    private static final Logger logger = LoggerFactory.getLogger(ServerTask.class);
    private Socket socket = null;
    private BufferedReader reader;

    public ServerTask(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public Socket call() throws IOException {
        handleRequest();
        return socket;
    }

    public void handleRequest() throws IOException {
        logger.debug("wait msg...");
        String msg = reader.readLine();
        logger.debug("receive msg( " + socket.getInetAddress() + " ) : " + msg);
    }
}
