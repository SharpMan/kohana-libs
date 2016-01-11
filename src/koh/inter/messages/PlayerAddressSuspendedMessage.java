package koh.inter.messages;

/**
 * Created by Melancholia on 1/10/16.
 */
public class PlayerAddressSuspendedMessage extends PlayerSuspendedMessage {

    public final String address;

    public PlayerAddressSuspendedMessage(long time, int accountId, String ip) {
        super(time,accountId);
        this.address = ip;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlayerAddressSuspendedMessage{");
        sb.append("hours=").append(time);
        sb.append(", accountId=").append(accountId);
        sb.append(", ip=").append(address);
        sb.append('}');
        return sb.toString();
    }
}
