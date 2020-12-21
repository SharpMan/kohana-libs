package koh.patterns.observable;

import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;

import static koh.concurrency.ParallelStream.parallelStreamOn;

public class ForkJoinObservable<O> extends Observable<O> {

    private final ForkJoinPool parallelism;

    public ForkJoinObservable(ForkJoinPool parallelism) {
        this.parallelism = parallelism;
    }

    public ForkJoinObservable(int parallelism) {
        this(new ForkJoinPool(parallelism));
    }

    @Override
    public <T> void notify(final BiConsumer<O, T> consumer, final T value) {
        parallelStreamOn(observers.parallelStream(), parallelism)
                .forEach((observer) -> consumer.accept(observer, value));
    }

}
