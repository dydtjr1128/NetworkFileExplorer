package com.dydtjr1128.nfe;

import com.dydtjr1128.nfe.network.Client;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ClientDataHandler {
    public void onDataReceive(Client client, ByteBuffer byteBuffer, int result) throws InterruptedException, IOException;
}
