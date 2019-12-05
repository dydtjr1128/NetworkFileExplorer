package com.dydtjr1128.nfe.server.config;

public class Config {
    public static final int ASYNC_SERVER_PORT = 14411;
    public static final int ASYNC_FILE_SERVER_PORT = 14412;
    public static final int DEFAULT_THREAD_POOL_COUNT = Runtime.getRuntime().availableProcessors();
    public static final int FILE_THREAD_POOL_COUNT = 4;

    public static final String END_MESSAGE_MARKER = "|END";
    public static final String MESSAGE_DELIMITER = "|||";
    public static final String FILE_STORE_PATH = System.getProperty("user.dir") + "\\StoredFile\\";
}
