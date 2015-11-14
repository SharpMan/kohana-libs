package koh.concurrency;

import java.util.concurrent.atomic.AtomicReference;

public class MutexReference<T> {

    private final AtomicReference<T> value = new AtomicReference<>();

    private volatile long lastAccess = 0;

    public synchronized void sync(Runnable op) {
        op.run();
    }

    public void set(T newValue) {
        this.sync(() -> {
            if(newValue != null && this.value.get() != null)
                throw new AlreadyReferenced();

            try {
                if(this.value.get() == null)
                    this.onUnset();
                else {
                    lastAccess = System.currentTimeMillis();
                    onSet(newValue);
                }
            } finally {
                this.value.set(newValue);
            }
        });
    }

    protected void onSet(T newValue) { }

    protected void onUnset() { }

    public T get() {
        return value.get();
    }

    public boolean accessedAfter(long ms) {
        return System.currentTimeMillis() - lastAccess >= ms;
    }

    public boolean alive() {
        return value.get() != null;
    }
}
