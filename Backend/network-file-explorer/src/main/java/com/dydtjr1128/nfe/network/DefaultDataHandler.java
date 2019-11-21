package com.dydtjr1128.nfe.network;

import com.dydtjr1128.nfe.ClientDataHandler;
import com.dydtjr1128.nfe.protocol.BindingData;
import com.dydtjr1128.nfe.protocol.NFEProtocol;
import com.dydtjr1128.nfe.protocol.ProtocolConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DefaultDataHandler implements ClientDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDataHandler.class);

    @Override
    public void onDataReceive(Client client, ByteBuffer byteBuffer, int result) throws  IOException {
        byteBuffer.flip();
        BindingData bindingData = ProtocolConverter.convertData(byteBuffer);
        String message = "C:\\Windows\\Cursors";
        System.out.println(bindingData.getPayload());
        //logger.debug("[Message Receive(" + client.getClientIP() + ")] :\n " + bindingData.getPayload() + " connected : " + ClientManager.getInstance().getClientCount());

        client.writeStringMessage(NFEProtocol.GET_LIST, message);


        //message 받으면 웹소켓으로 전달
    }
}
