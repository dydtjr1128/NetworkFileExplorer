import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncFileExplorer implements Runnable {
    private static final String SEVER_IP = "192.168.190.103";
    private static final int SEVER_PORT = 14410;
    private final AsynchronousSocketChannel asc;
    private AtomicInteger atomicInteger;

    public AsyncFileExplorer() throws IOException, ExecutionException, InterruptedException {
        atomicInteger = new AtomicInteger(0);
        asc = initClientSocketChannel();
        asc.connect(new InetSocketAddress(SEVER_IP, SEVER_PORT), asc, new CompletionHandler<Void, AsynchronousSocketChannel>() {
            @Override
            public void completed(Void result, AsynchronousSocketChannel channel) {
                startRead(channel, atomicInteger);
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                System.out.println("err!");
            }
        });
    }

    public AsynchronousSocketChannel initClientSocketChannel() throws IOException {
        return AsynchronousSocketChannel.open();
    }

    @Override
    public void run() {
        AtomicInteger sendaAtomicInteger = new AtomicInteger(0);
        while (true) {
            startWrite(asc, "hello" + sendaAtomicInteger, sendaAtomicInteger);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void startRead(final AsynchronousSocketChannel sockChannel, final AtomicInteger messageRead) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);

        sockChannel.read(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {

            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                //message is read from server
                messageRead.getAndIncrement();

                //print the message
                System.out.println("Read message:" + new String(buf.array()));
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("fail to read message from server");
            }

        });
    }

    private void startWrite(final AsynchronousSocketChannel sockChannel, final String message, final AtomicInteger messageWritten) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        buf.put(message.getBytes());
        buf.flip();
        messageWritten.getAndIncrement();
        sockChannel.write(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                System.out.println("message send!");
                //NOTHING TO DO
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("Fail to write the message to server");
            }
        });
    }
}
