package com.dydtjr1128.nfe.protocol.core;

import com.dydtjr1128.nfe.protocol.*;
import com.dydtjr1128.nfe.server.Client;

import java.util.HashMap;
import java.util.Map;

public class ProtocolManager {
    private Map<Byte, Protocol> protocolMap;

    public ProtocolManager() {
        protocolMap = new HashMap<>();
        protocolInit();
    }

    private void protocolInit() {
        protocolMap.put(NFEProtocol.GET_LIST, new GetListProtocol());
        protocolMap.put(NFEProtocol.COPY, new CopyProtocol());
        protocolMap.put(NFEProtocol.MOVE, new MoveProtocol());
        protocolMap.put(NFEProtocol.DELETE, new DeleteProtocol());
        protocolMap.put(NFEProtocol.CHANGE_NAME, new ChangeNameProtocol());
        //file
        protocolMap.put(NFEProtocol.FILE_UPLOAD, new FileTransferServer2ClientProtocol());
        protocolMap.put(NFEProtocol.FILE_DOWNLOAD, new FileTransferClient2ServerProtocol());
    }

    public void executeProtocolToAdmin(Client client, BindingData bindingData) {
        protocolMap.get(bindingData.getProtocol()).executeProtocolToAdmin(client, bindingData);
    }
}
