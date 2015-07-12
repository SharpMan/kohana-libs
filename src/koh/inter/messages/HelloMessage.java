package koh.inter.messages;

import koh.inter.InterMessage;
import koh.inter.MessageEnum;
import koh.protocol.client.Message;
import org.apache.mina.core.buffer.IoBuffer;
import static koh.protocol.client.BufUtils.*;

/**
 *
 * @author Neo-Craft
 */
public class HelloMessage implements InterMessage {
    
    public static final int ID = 1;

    public String Key;

    public HelloMessage() {

    }

    public HelloMessage(String k) {
        this.Key = k;
    }

    @Override
    public int getMessageId() {
        return MessageEnum.HelloMessage.value();
    }

    @Override
    public void serialize(IoBuffer buf) {
        writeUTF(buf, this.Key);
    }

    @Override
    public void deserialize(IoBuffer buf) {
        this.Key = readUTF(buf);
    }

}
