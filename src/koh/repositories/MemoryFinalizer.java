package koh.repositories;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryFinalizer {

    private final ScheduledExecutorService executor;

    public MemoryFinalizer(long interval, TimeUnit ttlInterval) {
        this.executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::clean, interval, interval, ttlInterval);
    }

    private void clean() {
        System.runFinalization();
        System.gc();
        System.runFinalization();
        System.gc();
    }

    public void dispose() {
        executor.shutdownNow();
    }
}
