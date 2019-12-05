package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.admin.service.ApplicationContextProvider;
import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.NFEProtocol;
import com.dydtjr1128.nfe.protocol.core.ProtocolConverter;
import com.dydtjr1128.nfe.server.fileserver.TransferFileMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private AsynchronousSocketChannel socketChannel;
    private final Queue<TransferFileMetaData> filePathQueue;
    private ClientDataHandler dataHandler;
    private InetSocketAddress inetSocketAddress;
    private static boolean isWriting = false;
    private final static Queue<ByteBuffer> messageQueue = new LinkedList<>();
    private BlockingQueue<BindingData> resultBlockingQueue;

    private ClientManager clientManager;

    Client(AsynchronousSocketChannel channel, ClientDataHandler dataHandler) throws IOException {
        this.socketChannel = channel;
        this.dataHandler = dataHandler;
        if (channel.isOpen()) {
            inetSocketAddress = ((InetSocketAddress) socketChannel.getRemoteAddress());
        }
        clientManager = ApplicationContextProvider.getApplicationContext().getBean(ClientManager.class);
        resultBlockingQueue = new LinkedBlockingDeque<>();
        filePathQueue = new ConcurrentLinkedDeque<>();
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
    public void getDirectoriesByPath(String path) {
        writeStringMessage(NFEProtocol.GET_LIST, path);
    }

    public void changeFileName(String payload) {
        writeStringMessage(NFEProtocol.CHANGE_NAME, payload);
    }

    public void deleteFile(String filePath) {
        writeStringMessage(NFEProtocol.DELETE, filePath);
    }

    public void copyFile(String payload) {
        writeStringMessage(NFEProtocol.COPY, payload);
    }

    public void moveFile(String payload) {
        writeStringMessage(NFEProtocol.MOVE, payload);
    }

    public void uploadToClient(String serverFilePath, String clientFilePath) {
        setFilePathQueue(serverFilePath, clientFilePath);
        writeStringMessage(NFEProtocol.FILE_UPLOAD, clientFilePath);
    }

    public void downloadFromClient(String filePath) {
        writeStringMessage(NFEProtocol.FILE_DOWNLOAD, filePath);
    }

    public BlockingQueue<BindingData> getBlockingQueue() {
        return resultBlockingQueue;
    }

    public Queue<TransferFileMetaData> getFilePathQueue() {
        return filePathQueue;
    }

    private void setFilePathQueue(String serverPath, String clientPath) {
        synchronized (filePathQueue) {
            filePathQueue.add(new TransferFileMetaData(serverPath, clientPath));
        }
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
        Client client = this;
        ByteBuffer input = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
        if (!socketChannel.isOpen()) {
            return;
        }
        int len = 0;
        socketChannel.read(input, len, new CompletionHandler<Integer, Integer>() {
            @Override
            public void completed(Integer result, Integer length) {
                // 음수나 0이면 연결 종료
                if (result < 1) {
                    client.close();
                    logger.debug("[Closing connection to ] : " + client);
                    clientManager.removeClient(client);
                } else {
                    logger.debug("[Some data received from] : " + client.getClientURL());

                    if (length == 0) {
                        input.flip();
                        length = (int) input.getLong();
                        input.compact();
                    }
                    if (length + 1 <= input.position()) {
                        input.flip();
                        try {
                            dataHandler.onDataReceive(client, input, length);
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                        input.clear();
                        length = 0;
                    }
                    socketChannel.read(input, length, this);
                }
            }

            @Override
            public void failed(Throwable exc, Integer buffer) {
                close();
                clientManager.removeClient(client);
            }
        });
    }

    public BindingData getCorrectProtocol(Client client, HashSet<Byte> requestSet) throws InterruptedException {
        BindingData result;
        do {
            result = client.getBlockingQueue().take();
            if (!requestSet.contains(result.getProtocol()))
                client.getBlockingQueue().add(result);
            else
                break;
        } while (true);
        return result;
    }

    private void close() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
