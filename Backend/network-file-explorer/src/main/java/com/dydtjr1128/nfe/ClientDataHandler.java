package com.dydtjr1128.nfe;

import java.nio.ByteBuffer;

public interface ClientDataHandler {
    public void onDataReceive(Client client, ByteBuffer byteBuffer, int result);
}
