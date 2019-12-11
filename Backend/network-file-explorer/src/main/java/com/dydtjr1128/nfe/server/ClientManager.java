package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.server.config.StartOrder;
import com.dydtjr1128.nfe.server.model.AdminMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(StartOrder.CLIENT_MANAGER)
public class ClientManager {
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);
    private final Map<String, Client> clientsHashMap;

    @Autowired
    private AdminWebSocketManager adminWebSocketManager;

    void addClient(Client client) {
        String clientIP = client.getClientURL();
        clientsHashMap.put(clientIP, client);
        adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.ADD, clientIP));
    }

    void removeClient(String clientIP) {
        clientsHashMap.remove(clientIP);
        adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.REMOVE, clientIP));
    }

    public Map<String, Client> getClientsHashMap() {
        return clientsHashMap;
    }

    public ArrayList<String> getAllClients() {
        ArrayList<String> clients = new ArrayList<>();
        for (String key : clientsHashMap.keySet()) {
            clients.add(clientsHashMap.get(key).getClientURL());
        }
        return clients;
    }

    public Client getClientByIP(InetSocketAddress remoteAddress) {
        String clientIP = remoteAddress.getAddress().toString().substring(1);
        for (String key : clientsHashMap.keySet()) {
            Client client = clientsHashMap.get(key);
            if (client.getClientIP().contains(clientIP) && client.getFilePathQueue().size() > 0)
                return client;
        }
        return null;
    }

    public ClientManager() {
        clientsHashMap = new ConcurrentHashMap<>();
        System.out.println("order1!!!!");
    }

    public Client getClient(String clientIP) {
        return clientsHashMap.get(clientIP);
    }
}