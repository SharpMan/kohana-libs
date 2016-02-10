package koh.inter.messages;

import koh.inter.InterMessage;

/**
 *
 * @author Neo-Craft
 */
public class HelloMessage implements InterMessage {
    
    public final String authKey;

    public HelloMessage(String authKey) {
        this.authKey = authKey;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HelloMessage{");
        sb.append("authKey='").append(authKey).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
