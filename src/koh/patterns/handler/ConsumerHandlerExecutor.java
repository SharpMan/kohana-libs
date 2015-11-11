package koh.patterns.handler;

import koh.patterns.BreakPropagation;
import koh.patterns.handler.api.ConsumableHandleMethod;
import koh.patterns.handler.api.HandlerEmitter;

import java.util.*;
import java.util.stream.Stream;

public class ConsumerHandlerExecutor<E extends HandlerEmitter, S> {

    private final Map<Class<?>, List<ConsumableHandleMethod<E, S>>> handlers = new HashMap<>();

    public Stream<ConsumableHandleMethod<E, S>> getLambdas(Class<? extends S> source) {
        List<ConsumableHandleMethod<E, S>> callbacks = handlers.get(source);
        return callbacks == null
                ? Stream.empty()
                : callbacks.stream();
    }

    public void handle(E emitter, S source) throws Exception {
        Collection<ConsumableHandleMethod<E, S>> callbacks = handlers.get(source.getClass());
        if(callbacks == null)
            return;
        for(ConsumableHandleMethod<E, S> handler : callbacks) {
            try {
                handler.handle(emitter, source);
            }
            catch(Throwable tr) {
                if(tr.getCause() != null && tr.getCause() instanceof BreakPropagation)
                    return;
                throw new Exception(tr);
            }
        }
    }

    public void registerLambda(Class<? extends S> source, ConsumableHandleMethod<E, S> handle) {
        List<ConsumableHandleMethod<E, S>> callbacks = handlers.get(source);
        if( callbacks == null) {
            callbacks = new ArrayList<>();
            handlers.put(source, callbacks);
        }
        callbacks.add(handle);
    }

    void putHandlers(Map<Class<?>, List<ConsumableHandleMethod<E, S>>> handlers) {
        this.handlers.putAll(handlers);
    }
}
