package protocol;

import file.FileManager;
import protocol.core.BindingData;
import protocol.core.NFEProtocol;
import protocol.core.Protocol;
import protocol.core.ProtocolConverter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class DeleteProtocol extends Protocol {

    @Override
    public void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) throws IOException {
        ByteBuffer byteBuffer;
        if(FileManager.getInstance().deleteFile(bindingData.getPayload())){
            byteBuffer = ProtocolConverter.makeTransferData(NFEProtocol.DELETE, "s");
        } else {
            byteBuffer = ProtocolConverter.makeTransferData(NFEProtocol.DELETE, "f");
        }
        asc.write(byteBuffer);
    }
}
