package protocol;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

public abstract class Protocol {
    public abstract void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData) throws IOException;
}
