package koh.patterns.event;

import koh.patterns.BreakPropagation;

import java.lang.reflect.Method;

class EventMethodInvoker {
    private final Object instance;
    private final Method target;

    EventMethodInvoker(Object instance, Method target) {
        this.instance = instance;
        this.target = target;
    }

    public void call(Event event) throws BreakPropagation, Exception {
        target.invoke(instance, event);
    }
}