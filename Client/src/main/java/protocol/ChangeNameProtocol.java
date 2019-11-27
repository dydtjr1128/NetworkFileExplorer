package protocol;

import file.FileManager;
import file.FileMapper;
import protocol.core.BindingData;
import protocol.core.NFEProtocol;
import protocol.core.Protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class ChangeNameProtocol extends Protocol {

    @Override
    public void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) throws IOException {
        String temp[] = bindingData.getPayload().split("\\|");
        String fromPath = temp[0];
        String toPath = temp[1];
        boolean success = FileManager.getInstance().changeFileName(fromPath,toPath);
        ByteBuffer byteBuffer;
        if(FileManager.getInstance().changeFileName(fromPath,toPath)){
            byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.REQUEST_OK, "");
        } else {
            byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.REQUEST_FAIL, "");
        }
        asc.write(byteBuffer);
    }
}
