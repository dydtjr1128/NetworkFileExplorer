package protocol.core;

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

    public static ByteBuffer makeTransferData(byte protocol, String path) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
        System.out.println(path.length());
        byte[] compressedData = Snappy.compress(path);
        byteBuffer.putLong(compressedData.length);
        byteBuffer.put(protocol);
        System.out.println(compressedData.length + "s@@@@@@");
        byteBuffer.put(compressedData);
        byteBuffer.flip();
        return byteBuffer;
    }
}
