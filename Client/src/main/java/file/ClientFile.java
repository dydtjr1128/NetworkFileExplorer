package file;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class ClientFile {
    @SerializedName("f")
    private String fileName;
    @SerializedName("i")
    private boolean isDirectory;
    @SerializedName("m")
    private long modifiedDate;
    @SerializedName("t")
    private String type;
    @SerializedName("s")
    private int size;//KB

    public ClientFile(File file, String type) {
        this.fileName = file.getName().equals("")?file.getAbsolutePath():file.getName();
        this.isDirectory = file.isDirectory();
        this.modifiedDate = file.lastModified();
        this.type = type;
        this.size = isDirectory ? 0 : (int) (file.length() / (double)1024);
    }

    public ClientFile(Path file, String type) throws IOException {
        this.fileName = file.getFileName().toString();
        this.isDirectory = Files.isDirectory(file);
        this.modifiedDate = Files.getLastModifiedTime(file).toMillis();
        this.type = type;
        this.size = isDirectory ? 0 : (int) (Files.size(file) / 1024);
    }
}
