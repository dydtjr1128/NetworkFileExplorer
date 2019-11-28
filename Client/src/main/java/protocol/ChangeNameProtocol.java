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
        String name = temp[1];
        ByteBuffer byteBuffer;
        if (FileManager.getInstance().changeFileName(fromPath, name)) {
            byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.CHANGE_NAME, "s");
        } else {
            byteBuffer = NFEProtocol.makeTransferData(NFEProtocol.CHANGE_NAME, "f");
        }
        asc.write(byteBuffer);
    }
}
