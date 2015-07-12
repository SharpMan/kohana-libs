package koh.commons;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Neo-Craft
 */
public abstract class CancellableExecutorRunnable implements Runnable {

    private ScheduledFuture<?> myFuture;

    public CancellableExecutorRunnable(ScheduledExecutorService service, long delay, long period) {
        this.myFuture = service.scheduleWithFixedDelay(this, delay, delay, TimeUnit.MILLISECONDS);
    }

    public CancellableExecutorRunnable(ScheduledExecutorService service, long endCallBack) {
        this.myFuture = service.schedule(this, endCallBack, TimeUnit.MILLISECONDS);
    }

    public boolean cancel() {
        if (myFuture != null && !myFuture.isCancelled()) {
            myFuture.cancel(false);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public abstract void run();
}
