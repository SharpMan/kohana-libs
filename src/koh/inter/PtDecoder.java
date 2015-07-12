package koh.inter;

import java.nio.BufferUnderflowException;
import java.nio.charset.CharacterCodingException;
import koh.inter.messages.*;
import koh.protocol.client.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 *
 * @author Neo-Craft
 */
public class PtDecoder extends CumulativeProtocolDecoder {

    private static final int BIT_MASK = 3;
    private static final int BIT_RIGHT_SHIFT_LEN_PACKET_ID = 2;

    public static int getMessageLength(IoBuffer buf, int header) {
        switch (header & BIT_MASK) {
            case 0:
                return 0;
            case 1:
                return buf.get();
            case 2:
                return buf.getShort();
            case 3:
                return (((buf.get() & 255) << 16) + ((buf.get() & 255) << 8) + (buf.get() & 255));
            default:
                return -1;
        }
    }

    public static int getMessageId(int header) {
        return header >> BIT_RIGHT_SHIFT_LEN_PACKET_ID;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer buf, ProtocolDecoderOutput out) throws Exception {
        if (buf.remaining() < 2) {
            return false;
        }

        int header = buf.getShort(), messageLength = getMessageLength(buf, header);

        if (buf.remaining() < messageLength) {
            return false;
        }

        InterMessage message;

        switch (getMessageId(header)) {
            case HelloMessage.ID:
                message = new HelloMessage();
                break;
            case PlayerCommingMessage.ID:
                message = new PlayerCommingMessage();
                break;
            case 3:
                message = new ExpulseAccountMessage();
                break;
            case 4:
                message = new PlayerCreatedMessage();
                break;
            default:
                System.out.println("[ERROR] Unknown Inter Message Header " + (getMessageId(header)));
                session.close();
                return false;
        }
        try {
            message.deserialize(buf);
        }catch(BufferUnderflowException | CharacterCodingException e){
            return false;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        out.write(message);
        return true;
    }

}
