package com.dydtjr1128.nfe.server;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ClientDataHandler {
    void onDataReceive(Client client, ByteBuffer byteBuffer, int result) throws InterruptedException, IOException;
}
