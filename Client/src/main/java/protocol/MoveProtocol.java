package protocol;

import file.FileManager;
import protocol.core.BindingData;
import protocol.core.NFEProtocol;
import protocol.core.Protocol;
import protocol.core.ProtocolConverter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class MoveProtocol extends Protocol {

    @Override
    public void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) throws IOException {
        String[] temp = bindingData.getPayload().split("\\|");
        String fromPath = temp[0];
        String toPath = temp[1];
        ByteBuffer byteBuffer;
        if (FileManager.getInstance().moveFile(fromPath, toPath)) {
            byteBuffer = ProtocolConverter.makeTransferData(NFEProtocol.MOVE, "s");
        } else {
            byteBuffer = ProtocolConverter.makeTransferData(NFEProtocol.MOVE, "f");
        }
        asc.write(byteBuffer);
    }
}
