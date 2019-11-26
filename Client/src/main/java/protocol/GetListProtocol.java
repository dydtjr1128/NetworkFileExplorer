package protocol;

import com.google.gson.Gson;
import file.ClientFile;
import file.FileManager;
import protocol.core.BindingData;
import protocol.core.NFEProtocol;
import protocol.core.Protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;

public class GetListProtocol extends Protocol {

    @Override
    public void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) throws IOException {
        long t = System.currentTimeMillis();
        List<ClientFile> clientFiles = FileManager.getInstance().getListByPath(bindingData.getPayload());
        Gson gson = new Gson();
        String json = gson.toJson(clientFiles);
        System.out.println(json);
        ByteBuffer byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.GET_LIST, json);
        asc.write(byteBuffer);

        System.out.println(System.currentTimeMillis() - t + "@@@");
    }
}
