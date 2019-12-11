package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.admin.service.ApplicationContextProvider;
import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.ProtocolConverter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ReadHandler implements CompletionHandler<Integer, Integer> {
    private AsynchronousSocketChannel channel;
    private ByteBuffer byteBuffer;
    private ClientManager clientManager;
    private RequestController requestController;

    ReadHandler(AsynchronousSocketChannel channel, ByteBuffer buffer, RequestController requestController) {
        clientManager = ApplicationContextProvider.getApplicationContext().getBean(ClientManager.class);
        this.requestController = requestController;
        this.channel = channel;
        this.byteBuffer = buffer;
    }

    private String getClientURL() throws IOException {
        InetSocketAddress inetSocketAddress = ((InetSocketAddress) channel.getRemoteAddress());
        return inetSocketAddress.getAddress().toString().substring(1) + ":" + inetSocketAddress.getPort();
    }

    private void close() {
        try {
            if (channel.isOpen()) {
                String clientIP = getClientURL();
                channel.close();
                clientManager.removeClient(clientIP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Integer result, Integer readByte) {
        // 음수나 0이면 연결 종료
        System.out.println(result + "!!");
        if (result < 1) {
            close();
        } else {
            System.out.println("!!" + byteBuffer.position() + " " + byteBuffer.limit());
            if (readByte == 0) {
                byteBuffer.flip();
                readByte = (int) byteBuffer.getLong();
                byteBuffer.compact();
                System.out.println("!!" + readByte);
            }
            if (readByte + 1 <= byteBuffer.position()) {
                byteBuffer.flip();
                BindingData bindingData = null;
                try {
                    bindingData = ProtocolConverter.convertData(byteBuffer, readByte);
                    System.out.println(bindingData.getPayload() + "@ " + bindingData.getPayload());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bindingData == null || !requestController.addRequest(bindingData)) {
                    close();
                }
                byteBuffer.clear();
                readByte = 0;
            }
            channel.read(byteBuffer, readByte, this);
        }
    }

    @Override
    public void failed(Throwable exc, Integer buffer) {
        close();
    }
}
