package koh.inter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 *
 * @author Neo-Craft
 */
public class IntercomDecoder extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession session, IoBuffer buf, ProtocolDecoderOutput out) throws Exception {
        if (buf.remaining() < 4)
            return false;

        int messageLength = buf.getInt();

        if (buf.remaining() < messageLength)
            return false;

        int startPosition = buf.position();

        InterMessage message;
        try {
            message = (InterMessage)buf.getObject();
        }catch(Exception ignored){
            buf.position(startPosition).skip(messageLength);
            return true;
        }

        out.write(message);
        return true;
    }

}
