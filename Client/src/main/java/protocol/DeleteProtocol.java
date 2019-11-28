package protocol;

import file.FileManager;
import protocol.core.BindingData;
import protocol.core.NFEProtocol;
import protocol.core.Protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class DeleteProtocol extends Protocol {

    @Override
    public void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) throws IOException {
        ByteBuffer byteBuffer;
        if(FileManager.getInstance().deleteFile(bindingData.getPayload())){
            byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.DELETE, "s");
        } else {
            byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.DELETE, "f");
        }
        asc.write(byteBuffer);
    }
}
