package koh.patterns.handler;

import koh.patterns.BreakPropagation;
import koh.patterns.handler.api.HandleMethod;
import koh.patterns.handler.api.HandlerEmitter;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Stream;

public class SimpleHandlerExecutor<E extends HandlerEmitter> {

    private final Map<Class<? extends Annotation>, List<HandleMethod<E>>> handlers = new HashMap<>();

    public Stream<HandleMethod<E>> getLambdas(Class<? extends Annotation> source) {
        List<HandleMethod<E>> callbacks = handlers.get(source);
        return callbacks == null
                ? Stream.empty()
                : callbacks.stream();
    }

    public void handle(E emitter, Class<? extends Annotation> source) throws Exception {
        Collection<HandleMethod<E>> callbacks = handlers.get(source);
        if(callbacks == null)
            return;
        for(HandleMethod<E> handler : callbacks) {
            try {
                handler.handle(emitter);
            }
            catch(Throwable tr) {
                if(tr.getCause() != null && tr.getCause() instanceof BreakPropagation)
                    return;
                throw new Exception(tr);
            }
        }
    }

    public void registerLambda(Class<? extends Annotation> source, HandleMethod<E> handle) {
        List<HandleMethod<E>> callbacks = handlers.get(source);
        if( callbacks == null) {
            callbacks = new ArrayList<>();
            handlers.put(source, callbacks);
        }
        callbacks.add(handle);
    }

    void putHandlers( Map<Class<? extends Annotation>, List<HandleMethod<E>>> handlers) {
        for(Map.Entry<Class<? extends Annotation>, List<HandleMethod<E>>> element : handlers.entrySet()) {
            List<HandleMethod<E>> callbacks = this.handlers.get(element.getKey());
            if( callbacks == null) {
                callbacks = new ArrayList<>();
                this.handlers.put(element.getKey(), callbacks);
            }
            callbacks.addAll(element.getValue());
        }
    }

}
