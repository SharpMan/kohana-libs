package koh.concurrency;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WaitingQueue<T> {

    private final CopyOnWriteArrayList<T> queue;
    private final ScheduledExecutorService executor;
    private final Consumer<T> consumer;

    public WaitingQueue(int interval, Consumer<T> consumer) {
        this.queue = new CopyOnWriteArrayList<>();
        this.consumer = consumer;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(this::run, interval, interval, TimeUnit.MILLISECONDS);
    }

    private void run() {
        T token;
        synchronized(queue){
            while(queue.isEmpty()){
                try{
                    queue.wait();
                }catch(InterruptedException ignored){
                }
            }
            token = queue.remove(0);
        }
        try{
            if(token!=null){
                consumer.accept(token);
            }
        }catch(Exception ignored){
        }
    }

    public void dispose() {
        this.executor.shutdownNow();
    }

    public int position(T value) {
        return queue.indexOf(value) + 1;
    }

    public int size() {
        return queue.size();
    }

    public void remove(T value) {
        queue.remove(value);
    }

    public int push(T value) {
        synchronized(queue){
            queue.addIfAbsent(value);
            queue.notify();
            return size();
        }
    }
}
