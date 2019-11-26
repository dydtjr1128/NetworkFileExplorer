package com.dydtjr1128.nfe.protocol;

import com.dydtjr1128.nfe.file.ClientFile;
import com.dydtjr1128.nfe.network.Client;
import com.dydtjr1128.nfe.protocol.core.BindingData;
import com.dydtjr1128.nfe.protocol.core.Protocol;
import lombok.NoArgsConstructor;

import java.nio.channels.AsynchronousSocketChannel;

@NoArgsConstructor
public class GetListProtocol extends Protocol {

    @Override
    public void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) {
/*        List<ClientFile> clientFiles = FileManager.getInstance().getListByPath(bindingData.getPayload());
        Gson gson = new Gson();
        String json = gson.toJson(clientFiles);
        System.out.println(json);
        ByteBuffer byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.GET_LIST, json);
        byteBuffer.flip();
        asc.write(byteBuffer);*/
    }

    @Override
    public void executeProtocolToAdmin(Client client, BindingData bindingData) {
        client.getBlockingQueue().add(bindingData.getPayload());
    }
}
