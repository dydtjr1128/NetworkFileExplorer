package com.dydtjr1128.nfe.network;

import com.dydtjr1128.nfe.admin.service.ApplicationContextProvider;
import com.dydtjr1128.nfe.network.model.AdminMessage;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.*;


public class ClientManager {
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);
    private static ClientManager INSTANCE = null;
    public List<Client> clientsVector;
    public Map<String, Client> clientsHashMap;


    public static ClientManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addClient(Client client) {
        String clientIP = client.getClientIP();
        clientsVector.add(client);
        clientsHashMap.put(clientIP, client);
        AdminWebSocketManager.getInstance().writeToAdminPage(new AdminMessage(AdminMessage.ADD, clientIP));
    }

    public void removeClient(Client client) {
        String clientIP = client.getClientIP();
        clientsVector.remove(client);
        clientsHashMap.remove(clientIP);
        AdminWebSocketManager.getInstance().writeToAdminPage(new AdminMessage(AdminMessage.REMOVE, clientIP));
    }

    public int getClientCount() {
        return clientsVector.size();
    }

    private static class LazyHolder {
        private static final ClientManager INSTANCE = new ClientManager();
    }

    private ClientManager() {
        clientsVector = Collections.synchronizedList(new Vector<>(1024));
        clientsHashMap = Collections.synchronizedMap(new HashMap<>());
    }

}


/*
package com.dydtjr1128.nfe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ClientManager {
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);
    private static ClientManager INSTANCE = null;
    public BlockingQueue<SocketChannel> socketChannelBlockingQueue;
    public HashMap<String, SocketChannel> clientHashMap;


    private static class LazyHolder {
        private static final ClientManager INSTANCE = new ClientManager();
    }

    public static ClientManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private ClientManager() {
        socketChannelBlockingQueue = new ArrayBlockingQueue<>(1024);
        clientHashMap = new HashMap<>();
    }

    public void addSocketChannel(SocketChannel socketChannel) {
        try {
            String clientIP = ((InetSocketAddress) socketChannel.getRemoteAddress()).getHostName();
            socketChannelBlockingQueue.put(socketChannel);
            clientHashMap.put(clientIP, socketChannel);
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

}
*/
