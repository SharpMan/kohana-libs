package koh.inter.messages;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import koh.inter.InterMessage;
import koh.protocol.client.Message;
import org.apache.commons.codec.Charsets;
import org.apache.mina.core.buffer.IoBuffer;

/**
 *
 * @author Neo-Craft
 */
public class PlayerCommingMessage implements InterMessage {

    public static final int ID = 2;

    public String Ticket, CurrentIP;
    public Integer AccountID;
    public String Nickname, SecretQuestion, SecretAnswer, LastIP;
    public Byte Right;
    public Timestamp last_login;

    public PlayerCommingMessage() {

    }

    private final static CharsetEncoder IoBufferEncoder = Charsets.UTF_8.newEncoder();
    private final static CharsetDecoder IoBufferDecoder = Charsets.UTF_8.newDecoder();

    public static void putString(IoBuffer buffer, String value) throws CharacterCodingException {
        buffer.putInt((int) (value.length() * IoBufferEncoder.averageBytesPerChar()));
        buffer.putString(value, (int) (value.length() * IoBufferEncoder.averageBytesPerChar()), IoBufferEncoder);
    }

    public static String readString(IoBuffer buffer) throws CharacterCodingException {
        int size = buffer.getInt();
        if (size > buffer.remaining()) {
            throw new CharacterCodingException();
        }
        return buffer.getString(size, IoBufferDecoder);
    }

    public PlayerCommingMessage(String ticket, String CurrentIP, int id, String nn, String sq, String sa, String la, byte r, Timestamp ts) {
        this.Ticket = ticket;
        this.CurrentIP = CurrentIP;
        this.AccountID = id;
        this.Nickname = nn;
        this.SecretQuestion = sq;
        this.SecretAnswer = sa;
        this.LastIP = la;
        this.Right = r;
        this.last_login = ts;
    }

    @Override
    public int getMessageId() {
        return 2;
    }

    @Override
    public void serialize(IoBuffer buf) {
        try {
            putString(buf, this.Ticket);
            putString(buf, this.CurrentIP);
            buf.putInt(this.AccountID);
            putString(buf, this.Nickname);
            putString(buf, this.SecretQuestion);
            putString(buf, this.SecretAnswer);
            putString(buf, this.LastIP);
            buf.put(this.Right);
            buf.putObject(last_login);
            //buf.putLong(last_login.toInstant().toEpochMilli());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deserialize(IoBuffer buf) throws Exception {
            this.Ticket = readString(buf);
            this.CurrentIP = readString(buf);
            this.AccountID = buf.getInt();
            this.Nickname = readString(buf);
            this.SecretQuestion = readString(buf);
            this.SecretAnswer = readString(buf);
            this.LastIP = readString(buf);
            this.Right = buf.get();
            last_login = (Timestamp) buf.getObject();

    }

}
