package com.dydtjr1128.nfe.network;

import com.dydtjr1128.nfe.file.ClientFile;
import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import com.dydtjr1128.nfe.protocol.core.ProtocolConverter;
import com.dydtjr1128.nfe.protocol.core.ProtocolManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class DefaultDataHandler implements ClientDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDataHandler.class);
    int i=0;
    @Override
    public void onDataReceive(Client client, ByteBuffer byteBuffer, int result) throws IOException {
        byteBuffer.flip();
        try {
            BindingData bindingData = ProtocolConverter.convertData(byteBuffer);
            ProtocolManager.getInstance().executeProtocolToAdmin(client, bindingData);
        } catch (IOException e) {
            e.printStackTrace();
        }
       /* BindingData bindingData = ProtocolConverter.convertData(byteBuffer);
        Gson gson = new Gson();
        try {
            List<ClientFile> list = gson.fromJson(bindingData.getPayload(), new TypeToken<List<ClientFile>>() {
            }.getType());
            for(ClientFile file : list){
                System.out.println(file.toString());
            }
        }catch (Exception e){

        }

        String message = "C:\\Windows\\Cursors";
        //System.out.println(bindingData.getPayload());
        //logger.debug("[Message Receive(" + client.getClientIP() + ")] :\n " + bindingData.getPayload() + " connected : " + ClientManager.getInstance().getClientCount());
        if(i==0) {
            i++;
            client.writeStringMessage(NFEProtocol.GET_LIST, message);
        }
        */

        //message 받으면 웹소켓으로 전달
    }
}
