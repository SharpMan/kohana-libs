package koh.inter;

import koh.protocol.client.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author Neo-Craft
 */
public class PtEncoder implements org.apache.mina.filter.codec.ProtocolEncoder {

    private static final int DEFAULT_CAPACITY = 1024;
    private static final int BIT_RIGHT_SHIFT_LEN_PACKET_ID = 2;

    public static int computeTypeLen(int len) {
        return len > 65535 ? 3 : (len > 255 ? 2 : (len > 0 ? 1 : 0));
    }

    public static int subComputeStaticHeader(int messageId, int typeLen) {
        return messageId << BIT_RIGHT_SHIFT_LEN_PACKET_ID | typeLen;
    }

    public static void writeHeader(IoBuffer buf, InterMessage message, int len) {
        int typeLen = computeTypeLen(len), header = subComputeStaticHeader(message.getMessageId(), typeLen);

        buf.putShort((short) header);

        switch (typeLen) {
            case 1:
                buf.put((byte) len);
                break;

            case 2:
                buf.putShort((short) len);
                break;

            case 3:
                buf.put((byte) (len >> 16 & 255));
                buf.putShort((short) (len & 65535));
                break;
        }
    }

    @Override
    public void encode(IoSession arg0, Object arg1, ProtocolEncoderOutput arg2) throws Exception {
        if (!(arg1 instanceof InterMessage)) {
            throw new Exception("I can only encode Message");
        }

        InterMessage message = (InterMessage) arg1;

        IoBuffer msgBuf = IoBuffer.allocate(DEFAULT_CAPACITY);
        int length = 0;

        msgBuf.setAutoExpand(true);

        message.serialize(msgBuf);
        length = msgBuf.position();
        msgBuf.flip();

        IoBuffer buf = IoBuffer.allocate(2 + computeTypeLen(length) + length);

        writeHeader(buf, message, length);
        buf.put(msgBuf);

        buf.flip();

        arg2.write(buf);
    }

    @Override
    public void dispose(IoSession arg0) throws Exception {
    }
}
