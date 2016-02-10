package koh.inter.messages;

import koh.inter.InterMessage;

/**
 *
 * @author Neo-Craft
 */
public class ExpulseAccountMessage implements InterMessage {

    public final int accountId;

    public ExpulseAccountMessage(int accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExpulseAccountMessage{");
        sb.append("accountId=").append(accountId);
        sb.append('}');
        return sb.toString();
    }
}
