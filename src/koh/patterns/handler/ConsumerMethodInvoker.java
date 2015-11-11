package koh.patterns.handler;

import koh.patterns.handler.api.HandlerEmitter;
import koh.patterns.handler.context.Ctx;
import koh.patterns.handler.context.RequireContexts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ConsumerMethodInvoker {
    private final RequireContexts contexts;
    private final Object instance;
    private final Method target;

    ConsumerMethodInvoker(Object instance, RequireContexts contexts, Method target) {
        this.instance = instance;
        this.contexts = contexts;
        this.target = target;
    }

    public void call(HandlerEmitter emitter, Object source) throws InvocationTargetException, IllegalAccessException {
        if(contexts == null) {
            target.invoke(instance, emitter, source);
        } else if(emitter.getHandlerContext() != null) {
            for(Ctx context : contexts.value()) {
                if(emitter.getHandlerContext().getClass() == context.value()) {
                    target.invoke(instance, emitter, source);
                    break;
                }
            }
        }
    }
}