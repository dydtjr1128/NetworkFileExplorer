package file;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

import java.nio.file.attribute.BasicFileAttributes;

@AllArgsConstructor
public class ClientFile {
    @SerializedName("f")
    private final String fileName;
    @SerializedName("i")
    private final boolean isDirectory;
    @SerializedName("m")
    private final long modifiedDate;
    @SerializedName("t")
    private final String type;
    @SerializedName("s")
    private final int size;//KB

    public ClientFile(final String fileName, final BasicFileAttributes attributes) {
        this.fileName = fileName;
        this.isDirectory = attributes.isDirectory();
        this.modifiedDate = attributes.lastModifiedTime().toMillis();
        this.type = FileMapper.getInstance().getFileType(isDirectory, fileName);
        this.size = isDirectory ? -1 : (int) (attributes.size() / 1024);
    }
}
