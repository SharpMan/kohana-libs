package koh.inter.messages;

import koh.inter.InterMessage;

/**
 *
 * @author Neo-Craft
 */
public class PlayerCreatedMessage implements InterMessage {

    public final int currentCount;
    public final int accountId;

    public PlayerCreatedMessage(int currentCount, int accountId) {
        this.currentCount = currentCount;
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlayerCreatedMessage{");
        sb.append("currentCount=").append(currentCount);
        sb.append(", accountId=").append(accountId);
        sb.append('}');
        return sb.toString();
    }
}
