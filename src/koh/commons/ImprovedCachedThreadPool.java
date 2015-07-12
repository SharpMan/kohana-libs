package koh.commons;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Neo-Craft
 */
public class ImprovedCachedThreadPool extends ThreadPoolExecutor {

    private int maxInQueue;

    public ImprovedCachedThreadPool(int corePoolSize, int maximumPoolSize, int maxInQueue) {
        super(corePoolSize, maximumPoolSize, 60, TimeUnit.SECONDS, new SynchronousQueue<>());
        this.maxInQueue = maxInQueue;
    }

    @Override
    public void execute(Runnable task) {
        if (getActiveCount() >= getMaximumPoolSize() - 2 && getQueue().size() >= maxInQueue) {
            setMaximumPoolSize(getMaximumPoolSize() + 1);
        }
        super.execute(task);
    }
}
