/*package com.dydtjr1128.nfe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class ClientAccepter extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClientAccepter.class);
    private final static int SERVER_PORT = 14410;
    private Selector acceptSelector;
    private ServerSocketChannel ssc = null;
    private Vector<SocketChannel> socketChannelVector;
    private HashMap<String, SocketChannel> clientHashMap;

    ClientAccepter() {
        socketChannelVector = new Vector<>();
        clientHashMap = new HashMap<>();
        InetSocketAddress address = new InetSocketAddress(SERVER_PORT);
        try {
            //selector 생성
            acceptSelector = Selector.open();

            //selector을 등록한 서버 소켓 채널 등록 및 세팅
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(address);

            //selector 등록
            ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            stopServer();
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopServer() {
        if (!ssc.isOpen()) return;

        try {
            Iterator<SocketChannel> iterator = ClientManager.getInstance().socketChannelBlockingQueue.iterator();

            while (iterator.hasNext()) {
                SocketChannel client = iterator.next();
                client.close();
                iterator.remove();
            }

            if (ssc != null && ssc.isOpen()) {
                ssc.close();
            }

            if (acceptSelector != null && acceptSelector.isOpen()) {
                acceptSelector.close();
            }
            logger.error("Server closed.");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            logger.info("[서버 시작...]");
            while (true) {

                int keyCount = acceptSelector.select();
                if (keyCount == 0) continue;

                Iterator<SelectionKey> iterator = acceptSelector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        *//* 연결 수락 작업 처리 *//*
                        registerClient(key);
                    }
                    *//* 데이터 Read 작업 처리는 다른 쓰레드로 위임*//*
                    iterator.remove();
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                ssc.close();
            } catch (IOException e1) {
                logger.error(e1.getMessage());
            }
        }
    }


    public void registerClient(SelectionKey key) {
        try {
            ServerSocketChannel readyChannel =
                    (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = readyChannel.accept();
            logger.info("[클라이언트 연결됨 : " + socketChannel.getRemoteAddress() + "]");
            ClientManager.getInstance().addSocketChannel(socketChannel);
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}*/


/*
package com.dydtjr1128.nfe;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MainServer extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(MainServer.class);
    private final static int SERVER_PORT = 14410;

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        try (
                ServerSocket server = new ServerSocket(SERVER_PORT)) {
            while (true) {

                try {
                    logger.warn("클라이언트 대기중...");
                    Socket connection = server.accept();
                    logger.warn("클라이언트 연결! (" + connection.getRemoteSocketAddress()+")");
                    Callable<Void> task = new ServerTask(connection);
                    pool.submit(task);


                } catch (IOException e) {
                    logger.warn("스타트 서버에 연결할 수 없습니다." + Arrays.toString(e.getStackTrace()));
                }

            }

        } catch (IOException e) {
            logger.warn("스타트 서버에 연결할 수 없습니다.");
        } finally {
            pool.shutdown();
        }
    }
}
*/
