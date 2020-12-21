package koh.inter;

import koh.protocol.client.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author Neo-Craft
 */
public class IntercomEncoder implements ProtocolEncoder {

    private static final int DEFAULT_CAPACITY = 512;

    public static void writeHeader(IoBuffer buf, int len) {
        buf.putInt(len);
    }

    @Override
    public void encode(IoSession session, Object input, ProtocolEncoderOutput output) throws Exception {
        if (!(input instanceof InterMessage)) {
            throw new Exception("I can only encode InterMessage");
        }

        InterMessage message = (InterMessage) input;

        IoBuffer msgBuffer = IoBuffer.allocate(DEFAULT_CAPACITY)
                .setAutoExpand(true);

        writeHeader(msgBuffer, 0);
        msgBuffer.putObject(message);

        int endOffset = msgBuffer.position();

        writeHeader(msgBuffer.position(0), endOffset - 4);

        msgBuffer.position(endOffset).flip();
        output.write(msgBuffer);
    }

    @Override
    public void dispose(IoSession arg0) throws Exception {
    }
}
