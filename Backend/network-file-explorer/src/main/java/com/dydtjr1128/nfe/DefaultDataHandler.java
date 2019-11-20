package com.dydtjr1128.nfe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DefaultDataHandler implements ClientDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDataHandler.class);
    private Charset charset;
    DefaultDataHandler() {
        charset = StandardCharsets.UTF_8;
    }

    @Override
    public void onDataReceive(Client client, ByteBuffer byteBuffer, int result) {
        byteBuffer.flip();
        String message = charset.decode(byteBuffer).toString();
        logger.debug("[Message Receive(" + client.getClientIP()+")] : " + message);
        //message 받으면 웹소켓으로 전달
    }
}
