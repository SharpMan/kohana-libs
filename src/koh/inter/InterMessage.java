package koh.inter;

import org.apache.mina.core.buffer.IoBuffer;

/**
 *
 * @author Neo-Craft
 */
public interface InterMessage {

    int getMessageId();

    void serialize(IoBuffer buf);

    void deserialize(IoBuffer buf) throws Exception;

}
