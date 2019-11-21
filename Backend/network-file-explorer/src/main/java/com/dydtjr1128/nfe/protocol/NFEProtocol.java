package com.dydtjr1128.nfe.protocol;

import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NFEProtocol {
    /* sever -> client  header */
    public static final byte GET_LIST = 0; // get directory list
    public static final byte COPY = 1;// copy file from to
    public static final byte MOVE = 2;// move file from to
    public static final byte DELETE = 3;//delete file path
    public static final byte CHANGE_NAME = 4;//change path name
    public static final byte CHECK_FILE_PATH = 5;//check exist file path to name
    //file
    public static final byte FILE_UPLOAD = 6;//upload file path
    public static final byte FILE_DOWNLOAD = 7;//download file path

    /* client -> server response header */
    public static final byte REQUEST_OK = 8;//request success
    public static final byte REQUEST_FAIL = 9;//request fail
    public static final int NETWORK_BYTE = 1024*1024;
    public static ByteBuffer makeTransferData(byte protocol, String path) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(NFEProtocol.NETWORK_BYTE);
        byte[] compressedData = Snappy.compress(path);
        byteBuffer.putLong(compressedData.length);
        byteBuffer.put(protocol);
        System.out.println(compressedData.length + "s@@@@@@");
        byteBuffer.put(compressedData);
        return byteBuffer;
    }
}
