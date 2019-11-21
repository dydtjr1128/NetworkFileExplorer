package com.dydtjr1128.nfe.protocol;

import java.nio.channels.AsynchronousSocketChannel;

public class GetListProtocol extends Protocol {

    @Override
    public void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) {
        /*List<ClientFile> clientFiles = FileManager.getInstance().getListByPath(bindingData.getPayload());
        Gson gson = new Gson();
        String json = gson.toJson(clientFiles);
        System.out.println(json);
        ByteBuffer byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.GET_LIST, json);
        byteBuffer.flip();
        asc.write(byteBuffer);*/
    }
}
