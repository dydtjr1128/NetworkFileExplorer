package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.ProtocolConverter;
import com.dydtjr1128.nfe.protocol.core.ProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DefaultDataHandler implements ClientDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDataHandler.class);
    @Override
    public void onDataReceive(Client client, ByteBuffer byteBuffer, int result) throws IOException {
        try {
            BindingData bindingData = ProtocolConverter.convertData(byteBuffer,result);
            ProtocolManager.getInstance().executeProtocolToAdmin(client, bindingData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
