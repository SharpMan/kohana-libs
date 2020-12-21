package koh.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryFinalizer {

    private static final Logger logger = LoggerFactory.getLogger(MemoryFinalizer.class);

    private final ScheduledExecutorService executor;

    public MemoryFinalizer(long interval, TimeUnit ttlInterval) {
        this.executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::clean, interval, interval, ttlInterval);
    }

    private void clean() {
        logger.debug("Running finalization cycle...");
        long time = System.currentTimeMillis();

        System.runFinalization();
        System.gc();
        System.runFinalization();
        System.gc();

        logger.debug("Finalization cycle done in " + (System.currentTimeMillis()-time) + " ms");
    }

    public void dispose() {
        executor.shutdownNow();
    }
}
