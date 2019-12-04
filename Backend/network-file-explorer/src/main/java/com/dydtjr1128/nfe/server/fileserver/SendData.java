package com.dydtjr1128.nfe.server.fileserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
@AllArgsConstructor
public class SendData {
    long readPosition;
    ByteBuffer buffer;

    void addPosition(int position) {
        readPosition += position;
    }
}
