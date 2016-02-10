package koh.patterns.observable;

import java.util.function.BiConsumer;

public class SimpleObservable<O> extends Observable<O> {

    @Override
    public <T> void notify(final BiConsumer<O, T> consumer, final T value) {
        this.stream().forEach((observer) -> consumer.accept(observer, value));
    }

}
