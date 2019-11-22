package com.dydtjr1128.nfe.file;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;

import java.io.File;

@ToString
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
        this.fileName = file.getName();
        this.isDirectory = file.isDirectory();
        this.modifiedDate = file.lastModified();
        this.type = type;
        this.size = isDirectory ? 0 : (int) (file.length() / 1024);
    }
}
