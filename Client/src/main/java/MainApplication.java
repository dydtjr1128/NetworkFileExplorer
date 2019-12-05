import com.google.gson.Gson;
import config.Config;
import config.ClientStarter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainApplication {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader;
        if(args.length == 1){
            bufferedReader = new BufferedReader(new FileReader(args[0]));
        } else {
            String path = System.getProperty("user.dir");
            bufferedReader = new BufferedReader(new FileReader(path + "/src/main/resources/config.json"));
        }

        Gson gson = new Gson();
        Config config = gson.fromJson(bufferedReader, Config.class);
        ClientStarter serverStarter = new ClientStarter();
        serverStarter.startClientByConfig(config);

    }
}
