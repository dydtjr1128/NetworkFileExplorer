package config;

import client.AsyncFileExplorer;
import file.AsyncFileTransferFactory;

import java.io.IOException;

public class ClientStarter {
    public void startClientByConfig(Config config) throws IOException {
        for (int i = 0; i <config.getClientCount() ; i++) {
            AsyncFileTransferFactory.getInstance().fileTransferManagerInitialize(config.getServerIp(),config.getFileServerPort());
            new AsyncFileExplorer(config.getServerIp(),config.getServerPort());
            System.out.println("Start " + i + " client.");
        }
    }
}
