package com.dydtjr1128.nfe.protocol.core;

import com.dydtjr1128.nfe.server.AdminWebSocketManager;
import com.dydtjr1128.nfe.server.Client;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.channels.AsynchronousSocketChannel;

public abstract class Protocol {
    private Gson gson;
    protected Protocol(){
        gson = new Gson();
    }

    public Gson getGson(){return gson;}
    public abstract void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData);
    public abstract void executeProtocolToAdmin(Client client, BindingData bindingData);
}
