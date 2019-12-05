package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.server.model.AdminMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ClientManager {
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);
    private static ClientManager INSTANCE = null;
    public final Map<String, Client> clientsHashMap;//If want to use one client one IP


    public static ClientManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void addClient(Client client) {
        String clientIP = client.getClientURL();
        clientsHashMap.put(clientIP, client);
        AdminWebSocketManager.getInstance().writeToAdminPage(new AdminMessage(AdminMessage.ADD, clientIP));
    }

    public void removeClient(Client client) {
        String clientIP = client.getClientURL();
        clientsHashMap.remove(clientIP);
        AdminWebSocketManager.getInstance().writeToAdminPage(new AdminMessage(AdminMessage.REMOVE, clientIP));
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

    private static class LazyHolder {
        private static final ClientManager INSTANCE = new ClientManager();
    }

    private ClientManager() {
        clientsHashMap = new ConcurrentHashMap<>();
    }

}