package koh.repositories;

import java.lang.ref.WeakReference;

public class RepositoryReference<T> {

    private volatile T value;

    private volatile long lastAccess = 0;

    public synchronized void sync(Runnable op) {
        op.run();
    }

    void set(T newValue) {
        if(newValue == null)
            throw new RuntimeException("newValue should not be null");

        if(value == null)
            lastAccess = System.currentTimeMillis();

        this.value = newValue;
    }

    void reused() {
        lastAccess = System.currentTimeMillis();
    }

    void unset() {
        this.value = null;
    }

    public T get() {
        return value;
    }

    public boolean accessedAfter(long ms) {
        return System.currentTimeMillis() - lastAccess >= ms;
    }

    public boolean loaded() {
        return value != null;
    }

}
