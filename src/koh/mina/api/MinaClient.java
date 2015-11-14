package koh.mina.api;

import koh.patterns.handler.api.HandlerEmitter;
import koh.patterns.handler.context.Context;
import koh.utils.LambdaCloseable;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class MinaClient implements HandlerEmitter {

    protected final IoSession session;

    public MinaClient(IoSession session, Context defaultContext) {
        this.session = session;
        this.context = new AtomicReference<>(defaultContext);
    }

    /**
     * @return the client's lifetime in milliseconds
     */
    public long lifetime() {
        return System.currentTimeMillis() - session.getCreationTime();
    }

    /**
     * @return true if and only if this client is being disconnected (but not disconnected yet) or is disconnected.
     */
    public boolean disconnecting() {
        return session.isClosing();
    }

    /**
     * @return true if the remote peer is really connected to this client.
     */
    public boolean connected() {
        return session.isConnected();
    }

    /**
     * @return Server host address
     */
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) session.getLocalAddress();
    }

    /**
     * @return client host address
     */
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) session.getRemoteAddress();
    }

    /**
     * Disconnects the client
     * @param waitPendingMessages Wait pending WRITE messages to be written before closing
     * @return Future of the action
     */
    public CloseFuture disconnect(boolean waitPendingMessages) {
        return session.close(!waitPendingMessages);
    }

    /**
     * @param message Message to send
     * @return Future of the action
     */
    public WriteFuture write(Object message) {
        return session.write(message);
    }

    /**
     * Disconnects immediately the client without waiting pending WRITE messages
     * @return Future of the action
     */
    public CloseFuture disconnect() {
        return this.disconnect(false);
    }

    /**
     * Disconnects the client after sending a message
     * @param message Message for describing the disconnection
     * @return Future of the action
     */
    public WriteFuture disconnect(Object message) {
        return this.write(message)
                .addListener(IoFutureListener.CLOSE);
    }

    /**
     * @return average of messages read from client per second
     */
    public double readMessagesThroughput() {
        return session.getReadMessagesThroughput();
    }

    /**
     * @return average of messages written to client per second
     */
    public double writtenMessagesThroughput() {
        return session.getWrittenMessagesThroughput();
    }

    /**
     * Suspends reads of the messages sent by the client
     * @return AutoCloseable for resuming reads
     */
    public LambdaCloseable suspendReads() {
        session.suspendRead();
        return session::resumeRead;
    }

    /**
     * Suspends writes of the messages sent to the client
     * @return AutoCloseable for resuming writes
     */
    public LambdaCloseable suspendWrites() {
        session.suspendWrite();
        return session::resumeWrite;
    }

    private final AtomicReference<Context> context;

    @Override
    public void setHandlerContext(Context context) {
        this.context.lazySet(context);
    }

    @Override
    public Context getHandlerContext() {
        return context.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinaClient that = (MinaClient) o;

        return session.equals(that.session);
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }
}
