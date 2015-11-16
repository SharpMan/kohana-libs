package koh.concurrency;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WaitingQueue<T> {

    private final CopyOnWriteArrayList<T> queue;
    private final ScheduledExecutorService treatmentExecutor;
    private final ScheduledExecutorService progressExecutor;

    private final Consumer<T> treater;
    private final BiConsumer<Integer, T> onChange;

    public WaitingQueue(int treatmentInterval, int progressInterval, Consumer<T> treater, BiConsumer<Integer, T> onChange) {
        this.queue = new CopyOnWriteArrayList<>();
        this.treater = treater;
        this.onChange = onChange;

        this.treatmentExecutor = Executors.newSingleThreadScheduledExecutor();
        this.progressExecutor = Executors.newSingleThreadScheduledExecutor();

        treatmentExecutor.scheduleWithFixedDelay(this::run, treatmentInterval, treatmentInterval, TimeUnit.MILLISECONDS);
        progressExecutor.scheduleWithFixedDelay(this::signalProgress, progressInterval, progressInterval, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unchecked")
    private void signalProgress() {
        synchronized(queue) {
            while (queue.isEmpty()) {
                try {
                    queue.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
        T[] values = (T[]) queue.toArray();
        for(int i =0; i < values.length; ++i)
            onChange.accept(i+1, values[i]);
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
                treater.accept(token);
            }
        }catch(Exception ignored){
        }
    }

    public void dispose() {
        this.progressExecutor.shutdownNow();
        this.treatmentExecutor.shutdownNow();
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
