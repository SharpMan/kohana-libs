package koh.mina;

import koh.mina.api.ClientSupplier;
import koh.mina.api.MinaClient;
import koh.mina.api.MinaListener;
import koh.mina.api.annotations.Connect;
import koh.mina.api.annotations.Disconnect;
import koh.mina.api.annotations.InactiveTimeout;
import koh.patterns.handler.ConsumerHandlerExecutor;
import koh.patterns.handler.SimpleHandlerExecutor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.util.ConcurrentHashSet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static koh.concurrency.ParallelStream.parallelStreamOn;

public class MinaServer<C extends MinaClient, M> {

    private final NioSocketAcceptor acceptor;
    private final ForkJoinPool broadcaster;

    private final SimpleHandlerExecutor<C> actions;
    private final ConsumerHandlerExecutor<C, M> messagesReception;
    private final MinaListener<C> listener;

    private final ClientSupplier<C> clientSupplier;
    private final Class<M> rootMessagesClass;

    private final Set<C> clients = new ConcurrentHashSet<>();

    public MinaServer(int processorsCount, ClientSupplier<C> clientSupplier, SimpleHandlerExecutor<C> actions,
                      ConsumerHandlerExecutor<C, M> messagesReception, MinaListener<C> listener,
                      Class<M> rootMessagesClass) {

        this.acceptor = new NioSocketAcceptor(processorsCount);
        this.broadcaster = new ForkJoinPool(processorsCount);

        this.actions = actions;
        this.messagesReception = messagesReception;
        this.listener = listener;

        this.clientSupplier = clientSupplier;
        this.rootMessagesClass = rootMessagesClass;
    }

    public MinaServer(ClientSupplier<C> clientSupplier, SimpleHandlerExecutor<C> actions,
                      ConsumerHandlerExecutor<C, M> messagesReception, MinaListener<C> listener,
                      Class<M> rootMessagesClass) {

        this(Runtime.getRuntime().availableProcessors() * 4, clientSupplier, actions,
                messagesReception, listener, rootMessagesClass);
    }

    public void configure(ProtocolDecoder decoder, ProtocolEncoder encoder, int minReadSize, int maxReadSize, int inactiveTimeoutSeconds, boolean disableTcpDelays) {
        acceptor.setReuseAddress(true);
        acceptor.setBacklog(100000);

        this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(encoder, decoder));
        this.acceptor.setHandler(new MinaHandler());

        this.acceptor.getSessionConfig().setMaxReadBufferSize(maxReadSize);
        this.acceptor.getSessionConfig().setMinReadBufferSize(minReadSize);
        this.acceptor.getSessionConfig().setReaderIdleTime(inactiveTimeoutSeconds);
        this.acceptor.getSessionConfig().setTcpNoDelay(disableTcpDelays);
        this.acceptor.getSessionConfig().setKeepAlive(true);
    }

    public void bind(int port) throws IOException {
        this.acceptor.bind(new InetSocketAddress(port));
    }

    public void bind(String host, int port) throws IOException {
        this.acceptor.bind(new InetSocketAddress(host, port));
    }

    public void unbind(int port) {
        this.acceptor.unbind(new InetSocketAddress(port));
    }

    public void unbind(String host, int port) {
        this.acceptor.unbind(new InetSocketAddress(host, port));
    }

    public void dispose() {
        acceptor.unbind();
        acceptor.dispose(true);
    }

    @SuppressWarnings("unchecked")
    private class MinaHandler extends IoHandlerAdapter {

        private final AttributeKey keyAttr = new AttributeKey(MinaClient.class, MinaServer.class.getName() + "$Clients.ATTR." + this.hashCode());

        private C sessionClient(IoSession session) {
            return (C)session.getAttribute(keyAttr);
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            C client = clientSupplier.supply(session);
            session.setAttribute(keyAttr, client);
            clients.add(client);
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            Object attr = session.getAttribute(keyAttr);
            if(attr != null)
                actions.handle((C)attr, Connect.class);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            Object attr = session.removeAttribute(keyAttr);
            if(attr != null) {
                try {
                    actions.handle((C) attr, Disconnect.class);
                } finally {
                    clients.remove(attr);
                }
            }
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            if(status == IdleStatus.READER_IDLE) {
                Object attr = session.getAttribute(keyAttr);
                if(attr != null)
                    actions.handle((C)attr, InactiveTimeout.class);
            }
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            Object attr = session.getAttribute(keyAttr);
            if(attr != null)
                listener.onException((C)attr, cause);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            if(message != null && rootMessagesClass.isAssignableFrom(message.getClass()))
                messagesReception.handle(sessionClient(session), (M)message);
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            listener.onMessageSent(sessionClient(session), message);
        }

    }

    public int getActualConnectedClients() {
        return clients.size();
    }

    public int getMaxConnectedClients() {
        return acceptor.getStatistics().getLargestManagedSessionCount();
    }

    public Stream<C> getClients() {
        return clients.stream();
    }

    public void broadcast(Object message) {
        parallelStreamOn(clients.parallelStream(), broadcaster)
                .forEach((client) -> client.write(message));
    }

    public void broadcast(Object message, Predicate<MinaClient> predicate) {
        parallelStreamOn(clients.parallelStream(), broadcaster).filter(predicate)
                .forEach((client) -> client.write(message));
    }

    public void broadcast(Stream<MinaClient> clients, Object message) {
        parallelStreamOn(clients, broadcaster)
                .forEach((client) -> client.write(message));
    }

    public void broadcastDisconnection(Object message) {
        parallelStreamOn(clients.parallelStream(), broadcaster)
                .forEach((client) -> client.disconnect(message));
    }

    public void broadcastDisconnection(Object message, Predicate<MinaClient> predicate) {
        parallelStreamOn(clients.parallelStream(), broadcaster).filter(predicate)
                .forEach((client) -> client.disconnect(message));
    }

    public void broadcastDisconnection(Stream<MinaClient> clients, Object message) {
        parallelStreamOn(clients, broadcaster)
                .forEach((client) -> client.disconnect(message));
    }

}
