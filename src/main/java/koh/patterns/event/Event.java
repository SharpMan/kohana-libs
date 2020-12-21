package koh.patterns.event;

public abstract class Event<E> {

    private final E target;

    public Event(E target) {
        this.target = target;
    }

    public E getTarget() {
        return this.target;
    }
}
