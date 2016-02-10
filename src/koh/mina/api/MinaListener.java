package koh.mina.api;

public interface MinaListener<C extends MinaClient> {

    void onException(C client, Throwable exception);

    void onMessageSent(C client, Object message);

    void onReceived(C client, Object message);
}
