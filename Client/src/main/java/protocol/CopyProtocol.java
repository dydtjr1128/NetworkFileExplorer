package protocol;

import file.FileManager;
import protocol.core.BindingData;
import protocol.core.NFEProtocol;
import protocol.core.Protocol;
import protocol.core.ProtocolConverter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class CopyProtocol extends Protocol {

    @Override
    public void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) throws IOException {
        String temp[] = bindingData.getPayload().split("\\|");
        String fromPath = temp[0];
        String toPath = temp[1];
        ByteBuffer byteBuffer;
        if (FileManager.getInstance().copyFile(fromPath, toPath)) {
            byteBuffer = ProtocolConverter.makeTransferData(NFEProtocol.COPY, "s");
        } else {
            byteBuffer = ProtocolConverter.makeTransferData(NFEProtocol.COPY, "f");
        }
        asc.write(byteBuffer);
    }
}
