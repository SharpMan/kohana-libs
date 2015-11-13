package koh.inter;

import java.nio.BufferUnderflowException;
import java.nio.charset.CharacterCodingException;
import java.util.HashMap;

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
public class IntercomDecoder extends CumulativeProtocolDecoder {

    private static final HashMap<Integer, Class<? extends InterMessage>> messages = new HashMap<Integer, Class<? extends InterMessage>>(){{
        put(InterMessageEnum.HelloMessage.value(), HelloMessage.class);
        put(InterMessageEnum.PlayerCommingMessage.value(), PlayerCommingMessage.class);
        put(InterMessageEnum.ExpulseAccount.value(), ExpulseAccountMessage.class);
        put(InterMessageEnum.PlayerCreated.value(), HelloMessage.class);
    }};

    @Override
    protected boolean doDecode(IoSession session, IoBuffer buf, ProtocolDecoderOutput out) throws Exception {
        if (buf.remaining() < 6)
            return false;

        int messageId = buf.getShort();
        int messageLength = buf.getInt();

        if (buf.remaining() < messageLength)
            return false;

        InterMessage message;
        try {
            message = messages.get(messageId).newInstance();
        }catch(Exception ignored){
            ignored.printStackTrace();
            buf.skip(messageLength);
            return true;
        }

        try {
            message.deserialize(buf);
        } catch(BufferUnderflowException | CharacterCodingException e){
            return false;
        }

        out.write(message);
        return true;
    }

}
