package com.dydtjr1128.nfe.protocol.core;

import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ProtocolConverter {
    public static BindingData convertData(ByteBuffer buffer, int result) throws IOException {
        System.out.println(result + " " + buffer.position() + " " + buffer.limit());
        byte protocol = buffer.get();
        byte[] bytes = new byte[result];
        buffer.get(bytes, 0, result);
        buffer.compact();

        String path = Snappy.uncompressString(bytes);
        System.out.println(result + " ! " + path);
        return new BindingData(result, protocol, path);
    }

    public static ByteBuffer makeTransferData(byte protocol, String path) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
        byte[] compressedData = Snappy.compress(path);
        byteBuffer.putLong(compressedData.length);
        byteBuffer.put(protocol);
        byteBuffer.put(compressedData);
        byteBuffer.flip();
        return byteBuffer;
    }
}
