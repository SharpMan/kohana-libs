package koh.patterns.observable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AsyncObservable<O> extends Observable<O> {

    private final ExecutorService executor;

    public AsyncObservable(ExecutorService executor) {
        this.executor = executor;
    }

    public AsyncObservable(int parallelism) {
        this(Executors.newFixedThreadPool(parallelism));
    }

    @Override
    public <T> void notify(final BiConsumer<O, T> consumer, final T value) {
        executor.execute(() -> this.stream().forEach((observer) -> consumer.accept(observer, value)));
    }
}
