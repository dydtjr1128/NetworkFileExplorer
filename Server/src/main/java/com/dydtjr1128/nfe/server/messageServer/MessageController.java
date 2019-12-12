package com.dydtjr1128.nfe.server.messageServer;

import com.dydtjr1128.nfe.protocol.BindingData;
import com.dydtjr1128.nfe.protocol.NFEProtocol;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageController {
    private LinkedBlockingQueue<BindingData>[] commandQueues;
    private int queueSize;

    public MessageController() {
        queueSize = NFEProtocol.CHANGE_NAME + 1;
        commandQueues = new LinkedBlockingQueue[queueSize];
        protocolInit();
    }

    private void protocolInit() {
        final int MAX_QUEUE_SIZE = 30;
        for (int i = 0; i < queueSize; i++) {
            commandQueues[i] = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
        }
    }

    public BindingData getRequestData(byte command) {
        try {
            return commandQueues[command].take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addRequest(BindingData bindingData) {
        try {
            commandQueues[bindingData.getProtocol()].put(bindingData);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
