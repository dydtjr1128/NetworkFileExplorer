package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.server.config.StartOrder;
import com.dydtjr1128.nfe.server.model.AdminMessage;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(StartOrder.WEBSOCKET_MANAGER)
public class AdminWebSocketManager {
    private Gson gson;
    @Autowired
    private SimpMessagingTemplate template;

    private static final String ADMIN_TOPIC = "/topic/admin";

    private AdminWebSocketManager() {
        gson = new Gson();
    }

    public void writeToAdminPage(AdminMessage message) {
        template.convertAndSend(ADMIN_TOPIC, gson.toJson(message));
    }
}
