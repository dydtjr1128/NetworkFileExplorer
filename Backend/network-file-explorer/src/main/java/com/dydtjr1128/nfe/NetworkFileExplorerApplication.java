package com.dydtjr1128.nfe;

import com.dydtjr1128.nfe.network.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class NetworkFileExplorerApplication {
    private static final Logger logger = LoggerFactory.getLogger(NetworkFileExplorerApplication.class);
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(NetworkFileExplorerApplication.class, args);

        ServerManager serverManager = context.getBean(ServerManager.class);
        try {
            serverManager.startServer();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
