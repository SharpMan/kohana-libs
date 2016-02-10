package koh.inter.messages;

import koh.inter.InterMessage;

/**
 * Created by Melancholia on 1/10/16.
 */
public class PlayerSuspendedMessage implements InterMessage {

    public final long time;
    public final int accountId;

    public PlayerSuspendedMessage(long time, int accountId) {
        this.time = time;
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlayerSuspendMessage{");
        sb.append("hours=").append(time);
        sb.append(", accountId=").append(accountId);
        sb.append('}');
        return sb.toString();
    }
}
