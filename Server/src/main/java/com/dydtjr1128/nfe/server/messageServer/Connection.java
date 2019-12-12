package com.dydtjr1128.nfe.server.messageServer;

import com.dydtjr1128.nfe.protocol.BindingData;
import com.dydtjr1128.nfe.protocol.NFEProtocol;
import com.dydtjr1128.nfe.protocol.ProtocolConverter;
import com.dydtjr1128.nfe.server.fileServer.TransferFileMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connection {
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    private AsynchronousSocketChannel socketChannel;
    private ConcurrentLinkedQueue<TransferFileMetaData> filePathQueue;
    private InetSocketAddress inetSocketAddress;
    private final Queue<ByteBuffer> messageQueue;
    private MessageController requestController;
    private boolean isWriting = false;

    public Connection(AsynchronousSocketChannel channel) throws IOException {
        this.socketChannel = channel;
        if (channel.isOpen()) {
            inetSocketAddress = ((InetSocketAddress) socketChannel.getRemoteAddress());
        }
        filePathQueue = new ConcurrentLinkedQueue<>();
        messageQueue = new LinkedList<>();
        requestController = new MessageController();
    }

    public String getClientURL() {
        return getClientIP() + ":" + inetSocketAddress.getPort();
    }

    public String getClientIP() {
        return inetSocketAddress.getAddress().toString().substring(1);
    }

    public void run() {
        read();
    }

    /* request function */
    public BindingData sendResponse(byte command, String payload) {
        writeStringMessage(command, payload);
        return getRequestData(command);
    }

    public void uploadToClient(String serverFilePath, String clientFilePath) {
        setFilePathQueue(serverFilePath, clientFilePath);
        writeStringMessage(NFEProtocol.FILE_UPLOAD, clientFilePath);
    }

    public void downloadFromClient(String filePath) {
        writeStringMessage(NFEProtocol.FILE_DOWNLOAD, filePath);
    }

    public Queue<TransferFileMetaData> getFilePathQueue() {
        return filePathQueue;
    }

    public BindingData getRequestData(byte command) {
        return requestController.getRequestData(command);
    }

    private void writeStringMessage(byte protocol, String msg) {
        try {
            ByteBuffer byteBuffer = ProtocolConverter.makeTransferData(protocol, msg);
            sendMessageToClient(byteBuffer);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void setFilePathQueue(String serverPath, String clientPath) {
        filePathQueue.add(new TransferFileMetaData(serverPath, clientPath));
    }

    private void sendMessageToClient(final ByteBuffer buffer) {
        synchronized (messageQueue) {
            messageQueue.add(buffer);
            if (isWriting) {
                return;
            }
        }
        loadAndWriteMessage();
    }

    private void loadAndWriteMessage() {
        ByteBuffer byteBuffer;
        synchronized (messageQueue) {
            byteBuffer = messageQueue.poll();
            isWriting = byteBuffer != null;
        }
        if (isWriting) {
            writeData(byteBuffer);
        }
    }

    private void writeData(ByteBuffer buffer) {
        socketChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer resultBuffer) {
                if (buffer.hasRemaining()) {
                    logger.debug("[Message write] : " + StandardCharsets.UTF_8.decode(resultBuffer).toString());
                    socketChannel.write(buffer, resultBuffer, this);
                } else {
                    loadAndWriteMessage();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }

    private void read() {
        ByteBuffer input = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
        if (!socketChannel.isOpen()) {
            return;
        }
        socketChannel.read(input, 0, new MessageReadHandler(socketChannel, input, requestController));
    }
}
