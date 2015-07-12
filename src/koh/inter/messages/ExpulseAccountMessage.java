package koh.inter.messages;

import koh.inter.InterMessage;
import koh.inter.MessageEnum;
import org.apache.mina.core.buffer.IoBuffer;

/**
 *
 * @author Neo-Craft
 */
public class ExpulseAccountMessage implements InterMessage {
    

    public int ID;

    public ExpulseAccountMessage() {

    }

    public ExpulseAccountMessage(int id) {
        this.ID = id;
    }

    @Override
    public int getMessageId() {
        return MessageEnum.ExpulseAccount.value();
    }

    @Override
    public void serialize(IoBuffer buf) {
        buf.putInt(ID);
    }

    @Override
    public void deserialize(IoBuffer buf) {
        this.ID = buf.getInt();
    }

}
