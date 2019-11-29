import com.google.gson.Gson;
import config.Config;
import config.ClientStarter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainApplication {
    public static void main(String[] args) throws IOException {
        String path = System.getProperty("user.dir");

        BufferedReader bufferedReader = new BufferedReader(new FileReader(path + "/src/main/resources/config.json"));

        Gson gson = new Gson();
        Config config = gson.fromJson(bufferedReader, Config.class);
        ClientStarter serverStarter = new ClientStarter();
        serverStarter.startClientByConfig(config);

    }
}
