package com.dydtjr1128.nfe.network;

import com.dydtjr1128.nfe.admin.service.ApplicationContextProvider;
import com.dydtjr1128.nfe.network.model.AdminMessage;
import com.google.gson.Gson;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminWebSocketManager {
    private Gson gson;
    private SimpMessagingTemplate template;

    private static AdminWebSocketManager INSTANCE = null;

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
        template.convertAndSend("/topic/admin", gson.toJson(message));
    }
}
