package file.sender;

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

    public void addPosition(int position) {
        readPosition += position;
    }
}
