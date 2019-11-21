package com.dydtjr1128.nfe.protocol;

import java.nio.channels.AsynchronousSocketChannel;

public abstract class Protocol {
    public abstract void executeProtocol(AsynchronousSocketChannel asc, BindingData bindingData);
}
