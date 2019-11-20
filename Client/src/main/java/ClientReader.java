/*
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

public class ClientReader extends Thread {
    private CharsetDecoder decoder = null;
    private Selector selector;

    public ClientReader(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        Charset charset = Charset.forName("UTF-8");
        decoder = charset.newDecoder();
        try {
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isReadable())
                        read(key);

                    iterator.remove();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void read(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

        try {
            int byteCount = socketChannel.read(byteBuffer);
            if (byteCount == -1) {
                throw new IOException();
            }
            byteBuffer.flip();
            String data = decoder.decode(byteBuffer).toString();
            System.out.println("Receive Message - " + data);
            FileExplorer.clearBuffer(byteBuffer);
        } catch (IOException ex) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
*/
