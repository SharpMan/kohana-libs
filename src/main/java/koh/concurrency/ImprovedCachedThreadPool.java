package koh.concurrency;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 *
 * @author Neo-Craft
 *
 */
public class ImprovedCachedThreadPool extends ThreadPoolExecutor {

    private final int maxInQueue;
    private final int baseMaxPoolSize;
    private final int basePoolSize;
    private final int hardLimit;

    public ImprovedCachedThreadPool(String name, int corePoolSize, int maximumPoolSize, int maxInQueue, int hardLimit) {
        super(corePoolSize, maximumPoolSize, 2, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
        this.setThreadFactory(new ThreadFactoryBuilder()
                .setNameFormat("improved-threadpool-" + name + "-%d").build());
        this.allowCoreThreadTimeOut(true);
        this.prestartAllCoreThreads();
        this.basePoolSize = corePoolSize;
        this.baseMaxPoolSize = maximumPoolSize;
        this.maxInQueue = maxInQueue;
        this.hardLimit = hardLimit;
    }

    public ImprovedCachedThreadPool(String name, int corePoolSize, int maximumPoolSize, int maxInQueue) {
        this(name, corePoolSize, maximumPoolSize, maxInQueue, 4*maximumPoolSize);
    }

    public ImprovedCachedThreadPool(String name, int corePoolSize, int maximumPoolSize) {
        this(name, corePoolSize, maximumPoolSize, 5);
    }

    private volatile long lastInactiveReduce;
    @Override
    public void execute(Runnable task) {
        try {
            int currentMaxSize = getMaximumPoolSize();
            int currentPoolSize = getPoolSize();
            int inQueue = getQueue().size();
            int activeCount = getActiveCount();
            //Active enlarge
            if (currentPoolSize < hardLimit && inQueue >= maxInQueue) {
                int toImprove = (inQueue/maxInQueue);
                if(toImprove > 0 && currentPoolSize + toImprove < hardLimit) {
                    if(currentPoolSize + toImprove > currentMaxSize)
                        setMaximumPoolSize(currentPoolSize + toImprove + 2);
                    setCorePoolSize(currentPoolSize + toImprove);
                }
            //inactive Reduce
            }else if(inQueue == 0 && activeCount <= basePoolSize
                    && (System.currentTimeMillis()-lastInactiveReduce > this.getKeepAliveTime(TimeUnit.MILLISECONDS))) {
                setCorePoolSize(basePoolSize);
                setMaximumPoolSize(baseMaxPoolSize);
                lastInactiveReduce = System.currentTimeMillis();
            //Active reduce
            } else if (inQueue <= 0 && currentMaxSize > baseMaxPoolSize && activeCount > basePoolSize) {
                int reduction = baseMaxPoolSize - basePoolSize;
                setCorePoolSize(currentPoolSize - reduction);
                setMaximumPoolSize(currentMaxSize - reduction);
            }
        } catch(Throwable ignored) {
        } finally {
            super.execute(task);
        }
    }
}
