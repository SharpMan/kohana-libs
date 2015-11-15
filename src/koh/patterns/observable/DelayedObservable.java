package koh.patterns.observable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class DelayedObservable<O> extends Observable<O> {

    private final ScheduledExecutorService scheduler;
    private final long delay;
    private final TimeUnit unit;

    public DelayedObservable(ScheduledExecutorService scheduler, long delay, TimeUnit unit) {
        this.scheduler = scheduler;
        this.delay = delay;
        this.unit = unit;
    }

    public DelayedObservable(int parallelism, long delay, TimeUnit unit) {
        this(Executors.newScheduledThreadPool(parallelism), delay, unit);
    }

    @Override
    public <T> void notify(final BiConsumer<O, T> consumer, final T value) {
        scheduler.schedule(() -> this.stream().forEach((observer) -> consumer.accept(observer, value)),
                delay, unit);
    }

}