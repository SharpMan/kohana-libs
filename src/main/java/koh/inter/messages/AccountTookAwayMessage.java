package koh.inter.messages;

import koh.inter.InterMessage;

/**
 * Created by Melancholia on 1/10/16.
 */
public class AccountTookAwayMessage implements InterMessage {

    public final int accountId;

    public AccountTookAwayMessage(int accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AccountTookAwayMessage{");
        sb.append("accountId='").append(accountId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}