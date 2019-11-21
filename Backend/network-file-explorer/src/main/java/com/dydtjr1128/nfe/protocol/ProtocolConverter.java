package com.dydtjr1128.nfe.protocol;

import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProtocolConverter {
    public static BindingData convertData(ByteBuffer buffer) throws IOException {
        long len = buffer.getLong();
        byte protocol = buffer.get();
        byte[] bytes = new byte[(int) len];
        buffer.get(bytes);
        String path = Snappy.uncompressString(bytes);
        return new BindingData(len, protocol, path);
    }
}
