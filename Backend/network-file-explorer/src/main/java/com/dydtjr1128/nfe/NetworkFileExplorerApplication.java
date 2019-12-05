package com.dydtjr1128.nfe;

import com.dydtjr1128.nfe.server.ServerStarter;
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

        ServerStarter serverManager = context.getBean(ServerStarter.class);
        try {
            serverManager.startServer();
        } catch (IOException e) {
            logger.error("", e);
        }
    }

}
