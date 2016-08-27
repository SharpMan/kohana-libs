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

        final int messageLength = buf.getInt();

        if (buf.remaining() < messageLength)
            return false;

        if(Math.abs(Integer.MAX_VALUE - messageLength) < 5){ //java.lang.OutOfMemoryError: Requested array size exceeds VM limit
            return true;
        }

        int startPosition = buf.position();

        final InterMessage message;
        try {
            message = (InterMessage) buf.getObject();
        }catch(Exception ignored){
            try {
                //buf.position(startPosition).skip(messageLength);
                if(buf.hasRemaining()){
                    buf.skip(buf.remaining());
                    return false;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            ignored.printStackTrace();
            return true;
        }

        out.write(message);
        return true;
    }

}
