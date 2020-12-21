package koh.patterns.handler.api;

import koh.patterns.BreakPropagation;

@FunctionalInterface
public interface ConsumableHandleMethod<E, S> {

    /**
     *
     * @param emitter Emitter of the action to handle
     * @param consumable object to consume
     * @throws BreakPropagation
     * @throws Exception
     */
    void handle(E emitter, S consumable) throws BreakPropagation, Exception;
}
