package com.dydtjr1128.nfe.server.connection;

import com.dydtjr1128.nfe.server.config.StartOrder;
import com.dydtjr1128.nfe.server.messageServer.AdminWebSocketManager;
import com.dydtjr1128.nfe.server.messageServer.Connection;
import com.dydtjr1128.nfe.server.model.AdminMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(StartOrder.CLIENT_MANAGER)
public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private final Map<String, Connection> clientsHashMap;
    private final AdminWebSocketManager adminWebSocketManager;

    @Autowired
    public ConnectionManager(AdminWebSocketManager adminWebSocketManager) {
        this.adminWebSocketManager = adminWebSocketManager;
        clientsHashMap = new ConcurrentHashMap<>();
    }

    public void addClient(Connection connection) {
        String clientIP = connection.getClientURL();
        clientsHashMap.put(clientIP, connection);
        adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.ADD, clientIP));
    }

    public void removeClient(String clientIP) {
        clientsHashMap.remove(clientIP);
        adminWebSocketManager.writeToAdminPage(new AdminMessage(AdminMessage.REMOVE, clientIP));
    }

    public ArrayList<String> getAllClients() {
        ArrayList<String> clients = new ArrayList<>();
        for (String key : clientsHashMap.keySet()) {
            clients.add(clientsHashMap.get(key).getClientURL());
        }
        return clients;
    }

    public Connection getClientByIP(InetSocketAddress remoteAddress) {
        String clientIP = remoteAddress.getAddress().toString().substring(1);
        for (String key : clientsHashMap.keySet()) {
            Connection connection = clientsHashMap.get(key);
            if (connection.getClientIP().contains(clientIP) && connection.getFilePathQueue().size() > 0)
                return connection;
        }
        return null;
    }

    public Connection getClient(String clientIP) {
        return clientsHashMap.get(clientIP);
    }
}