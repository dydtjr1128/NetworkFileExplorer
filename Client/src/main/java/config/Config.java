package config;

import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class Config {
    private String serverIp;
    private int serverPort;
    private int fileServerPort;
    private int clientCount;

    @Expose(serialize = false, deserialize = false)
    public static final String END_MESSAGE_MARKER = "@END";
    @Expose(serialize = false, deserialize = false)
    public static final String MESSAGE_DELIMITTER = "#";
}
