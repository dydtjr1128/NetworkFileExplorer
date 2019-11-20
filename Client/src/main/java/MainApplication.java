import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainApplication {
    public static void main(String[] args) {
        try {
            /*FileExplorer fileExplorer = new FileExplorer();
            fileExplorer.startClient();*/
            new Thread(new AsyncFileExplorer()).start();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
