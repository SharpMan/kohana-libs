package koh.patterns.observable;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public abstract class Observable<O> {

    protected final CopyOnWriteArrayList<O> observers = new CopyOnWriteArrayList<>();

    public void register(O obs) {
        observers.addIfAbsent(obs);
    }

    public void unregister(O obs) {
        observers.remove(obs);
    }

    public abstract <T> void notify(final BiConsumer<O, T> consumer, final T value);

    public Stream<O> stream() {
        return observers.stream();
    }

    public int registered() {
        return observers.size();
    }

}
