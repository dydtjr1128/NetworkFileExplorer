package protocol.core;

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
    //file
    public static final byte FILE_UPLOAD = 5;//upload file path
    public static final byte FILE_DOWNLOAD = 6;//download file path

    public static final int NETWORK_BYTE = 1024*1024;
}
