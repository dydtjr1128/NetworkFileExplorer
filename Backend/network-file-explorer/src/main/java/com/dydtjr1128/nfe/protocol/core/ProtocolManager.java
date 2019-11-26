package com.dydtjr1128.nfe.protocol.core;

import com.dydtjr1128.nfe.network.Client;
import com.dydtjr1128.nfe.protocol.*;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ProtocolManager {
    private static ProtocolManager INSTANCE = null;
    private Map<Byte, Protocol> protocolMap;

    public static ProtocolManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final ProtocolManager INSTANCE = new ProtocolManager();
    }

    private ProtocolManager() {
        protocolMap = new HashMap<>();
        protocolInit();
    }

    private void protocolInit() {
        protocolMap.put(NFEProtocol.GET_LIST, new GetListProtocol());
        protocolMap.put(NFEProtocol.COPY, new CopyProtocol());
        protocolMap.put(NFEProtocol.MOVE, new MoveProtocol());
        protocolMap.put(NFEProtocol.DELETE, new DeleteProtocol());
        protocolMap.put(NFEProtocol.CHANGE_NAME, new ChangeNameProtocol());
        protocolMap.put(NFEProtocol.CHECK_FILE_PATH, new CheckFilePathProtocol());
        //file
        protocolMap.put(NFEProtocol.FILE_UPLOAD, new FileUploadProtocol());
        protocolMap.put(NFEProtocol.FILE_DOWNLOAD, new FileDownloadProtocol());

        protocolMap.put(NFEProtocol.REQUEST_OK, new RequestOKProtocol());
        protocolMap.put(NFEProtocol.REQUEST_FAIL, new RequestFailProtocol());
    }

    public void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) {
        protocolMap.get(bindingData.getProtocol()).executeProtocol(asc, bindingData);
    }
    public void executeProtocolToAdmin(Client client, BindingData bindingData) {
        protocolMap.get(bindingData.getProtocol()).executeProtocolToAdmin(client, bindingData);
    }
}
