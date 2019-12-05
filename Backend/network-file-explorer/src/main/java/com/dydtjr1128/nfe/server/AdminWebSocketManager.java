package com.dydtjr1128.nfe.server;

import com.dydtjr1128.nfe.admin.service.ApplicationContextProvider;
import com.dydtjr1128.nfe.server.model.AdminMessage;
import com.google.gson.Gson;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminWebSocketManager {
    private Gson gson;
    private SimpMessagingTemplate template;

    private static AdminWebSocketManager INSTANCE = null;
    private static final String ADMIN_TOPIC = "/topic/admin";
    public static AdminWebSocketManager getInstance() {
        return AdminWebSocketManager.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final AdminWebSocketManager INSTANCE = new AdminWebSocketManager();
    }

    private AdminWebSocketManager() {
        gson = new Gson();
        this.template = ApplicationContextProvider.getApplicationContext().getBean(SimpMessagingTemplate.class);
    }

    public void writeToAdminPage(AdminMessage message){
        template.convertAndSend(ADMIN_TOPIC, gson.toJson(message));
    }
}
