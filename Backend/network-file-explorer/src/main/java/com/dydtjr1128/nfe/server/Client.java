package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import com.dydtjr1128.nfe.protocol.core.ProtocolConverter;
import com.dydtjr1128.nfe.server.fileserver.TransferFileMetaData;
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

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private AsynchronousSocketChannel socketChannel;
    private ConcurrentLinkedQueue<TransferFileMetaData> filePathQueue;
    private InetSocketAddress inetSocketAddress;
    private final Queue<ByteBuffer> messageQueue;
    private RequestController requestController;
    private boolean isWriting = false;

    Client(AsynchronousSocketChannel channel) throws IOException {
        this.socketChannel = channel;
        if (channel.isOpen()) {
            inetSocketAddress = ((InetSocketAddress) socketChannel.getRemoteAddress());
        }
        filePathQueue = new ConcurrentLinkedQueue<>();
        messageQueue = new LinkedList<>();
        requestController = new RequestController();
    }

    void writeStringMessage(byte protocol, String msg) {
        try {
            ByteBuffer byteBuffer = ProtocolConverter.makeTransferData(protocol, msg);
            sendMessageToClient(byteBuffer);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    String getClientURL() {
        return inetSocketAddress.getAddress().toString().substring(1) + ":" + inetSocketAddress.getPort();
    }

    String getClientIP() {
        return inetSocketAddress.getAddress().toString().substring(1);
    }

    void run() {
        read();
    }

    /* request function */
    public BindingData sendResponse(byte command, String payload){
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
        socketChannel.read(input, 0, new ReadHandler(socketChannel, input, requestController));
    }

    public BindingData getRequestData(byte command) {
        return requestController.getRequestData(command);
    }
}
