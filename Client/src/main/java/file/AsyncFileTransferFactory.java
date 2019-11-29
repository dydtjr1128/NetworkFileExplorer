package file;

import file.receiver.FileReceiver;
import file.sender.FileSender;

public class AsyncFileTransferFactory {
    public static AsyncFileTransferFactory getInstance() {
        return AsyncFileTransferFactory.LazyHolder.INSTANCE;
    }

    private String ip;
    private int port;

    public void fileTransferManagerInitialize(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public FileReceiver getReceiver() {//where file store
        return new FileReceiver(ip, port);
    }

    public FileSender getSender() {
        return new FileSender(ip, port);
    }

    private static class LazyHolder {
        private static final AsyncFileTransferFactory INSTANCE = new AsyncFileTransferFactory();
    }

    private AsyncFileTransferFactory() {
    }
}
