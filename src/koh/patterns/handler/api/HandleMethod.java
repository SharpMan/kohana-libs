package koh.patterns.handler.api;

import koh.patterns.BreakPropagation;

@FunctionalInterface
public interface HandleMethod<E> {

    /**
     *
     * @param emitter Emitter of the action to handle
     * @throws BreakPropagation
     * @throws Exception
     */
    void handle(E emitter) throws BreakPropagation, Exception;
}
