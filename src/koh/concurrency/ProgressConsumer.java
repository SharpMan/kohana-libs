package koh.concurrency;

@FunctionalInterface
public interface ProgressConsumer<T> {

    void signal(T client, int position, int total);
}
