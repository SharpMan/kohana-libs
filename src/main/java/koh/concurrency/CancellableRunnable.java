package koh.concurrency;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author Alleos13
 */
public abstract class CancellableRunnable implements Runnable {

    private Future<?> myFuture;
    private ThreadPoolExecutor Service;


    public CancellableRunnable(ThreadPoolExecutor service) {
        this.myFuture = service.submit(this);
        this.Service = service;
    }

    public boolean cancel() {
        if (myFuture != null && !myFuture.isCancelled()) {
            myFuture.cancel(false);
            this.Service.remove(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public abstract void run();
}
