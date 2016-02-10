package koh.mina.api;

import org.apache.mina.core.session.IoSession;

@FunctionalInterface
public interface ClientSupplier<T extends MinaClient> {

    T supply(IoSession session);

}
