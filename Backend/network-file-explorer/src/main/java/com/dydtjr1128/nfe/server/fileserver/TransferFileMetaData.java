package com.dydtjr1128.nfe.server.fileserver;

import lombok.Getter;

@Getter
public class TransferFileMetaData {
    private String severPath; // path+filename
    private String clientPath; // path+filename
    public TransferFileMetaData(String severPath, String clientPath){
        this.severPath = severPath;
        int idx = severPath.lastIndexOf("\\");
        this.clientPath = clientPath + "\\" + severPath.substring(idx + 1);
    }
}
