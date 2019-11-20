/*
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class FileExplorer extends Thread {
    private static final String SEVER_IP = "192.168.190.103";
    private Selector selector = null;
    private SocketChannel socketChannel = null;

    public void startClient() throws IOException {
        initClient();
        startReader();
        startWriter();
    }

    private void initClient() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(SEVER_IP, 14410));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void startReader() {
        ClientReader writer = new ClientReader(selector);
        writer.start();
    }

    private void startWriter() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        try {
            while (true) {
                byteBuffer.clear();
                byteBuffer.put("Hello nice to meet u.".getBytes());
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            clearBuffer(byteBuffer);
        }
    }

    static void clearBuffer(ByteBuffer buffer) {
        if (buffer != null) {
            buffer.clear();
        }
    }
}
*/
