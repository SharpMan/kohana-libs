package koh.patterns.event.api;

import koh.patterns.BreakPropagation;
import koh.patterns.event.Event;

@FunctionalInterface
public interface TreatEvent<E extends Event> {

    /**
     * @param event Event to treat
     */
    void treat(E event) throws BreakPropagation, Exception;
}
