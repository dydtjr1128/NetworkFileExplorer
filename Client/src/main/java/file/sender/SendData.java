package file.sender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
@AllArgsConstructor
class SendData {
    long readPosition;
    ByteBuffer buffer;

    void addPosition(int position) {
        readPosition += position;
    }
}
