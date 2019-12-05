package com.dydtjr1128.nfe.server.fileserver;

import com.dydtjr1128.nfe.server.config.Config;
import lombok.Getter;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Getter
class Attachment {
    private long readPosition;
    private AsynchronousFileChannel fileChannel;
    private String fileName;
    private long fileSize;

    Attachment() {
        readPosition = 0;
    }

    void calcFileData(String string) {
        String[] temp = string.replace(Config.END_MESSAGE_MARKER, "").split(Config.MESSAGE_DELIMITER.replace("|", "\\|"));
        fileName = temp[0];
        fileSize = Long.parseLong(temp[1]);
    }

    void addPosition(int position) {
        readPosition += position;
    }

    void openFileChannel(Path path) throws IOException {
        fileChannel = AsynchronousFileChannel.open(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
        );
    }
}