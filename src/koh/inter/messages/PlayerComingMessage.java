package koh.inter.messages;

import koh.inter.InterMessage;

import java.sql.Timestamp;

/**
 *
 * @author Neo-Craft
 */
public class PlayerComingMessage implements InterMessage {

    public final String authenticationTicket, authenticationAddress;
    public final Integer accountId;
    public final String nickname, secretQuestion, secretAnswer, lastAddress;
    public final Byte rights;
    public final Timestamp lastLogin;

    public PlayerComingMessage(String authenticationTicket, String authenticationAddress,
                               int accountId, String nickname, String secretQuestion, String secretAnswer,
                               String lastAddress, byte rights, Timestamp lastLogin) {
        this.authenticationTicket = authenticationTicket;
        this.authenticationAddress = authenticationAddress;
        this.accountId = accountId;
        this.nickname = nickname;
        this.secretQuestion = secretQuestion;
        this.secretAnswer = secretAnswer;
        this.lastAddress = lastAddress;
        this.rights = rights;
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlayerCommingMessage{");
        sb.append("authenticationAddress='").append(authenticationAddress).append('\'');
        sb.append(", accountId=").append(accountId);
        sb.append(", nickname='").append(nickname).append('\'');
        sb.append(", secretQuestion='").append(secretQuestion).append('\'');
        sb.append(", lastAddress='").append(lastAddress).append('\'');
        sb.append(", rights=").append(rights);
        sb.append(", lastLogin=").append(lastLogin);
        sb.append('}');
        return sb.toString();
    }
}
