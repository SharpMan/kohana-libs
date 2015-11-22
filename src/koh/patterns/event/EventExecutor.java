package koh.patterns.event;

import koh.patterns.BreakPropagation;
import koh.patterns.event.api.EventTreatmentPriority;
import koh.patterns.event.api.TreatEvent;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class EventExecutor {

    private final PrioritizedTreatEventComparator lambdasSorter = new PrioritizedTreatEventComparator();

    private final Map<Class<?>, List<PrioritizedTreatEvent>> listeners = new HashMap<>();
    private final ExecutorService executor;

    public EventExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public Future<?> fireFuture(Event event) {
        Collection<PrioritizedTreatEvent> callbacks = listeners.get(event.getClass());
        if(callbacks == null)
            return null;

        return executor.submit(this.callTreatments(callbacks, event));
    }

    public void fire(Event event) {
        Collection<PrioritizedTreatEvent> callbacks = listeners.get(event.getClass());
        if(callbacks == null)
            return;

        executor.execute(this.callTreatments(callbacks, event));
    }

    public boolean syncFire(Event event) {
        Collection<PrioritizedTreatEvent> callbacks = listeners.get(event.getClass());
        if(callbacks == null)
            return false;

        try {
            executor.submit(this.callTreatments(callbacks, event)).get();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Runnable callTreatments(Collection<PrioritizedTreatEvent> callbacks, Event event) {
        return () -> {
            for (PrioritizedTreatEvent prioritized : callbacks) {
                try {
                    prioritized.treatment.treat(event);
                }catch (Throwable tr) {
                    if(tr.getCause() != null && tr.getCause() instanceof BreakPropagation)
                        return;
                }
            }
        };
    }

    public Stream<PrioritizedTreatEvent> getLambdas(Class<? extends Event> source) {
        List<PrioritizedTreatEvent> callbacks = listeners.get(source);
        return callbacks == null
                ? Stream.empty()
                : callbacks.stream();
    }

    public void registerLambda(Class<Event> eventClass, TreatEvent<Event> treatment) {
        List<PrioritizedTreatEvent> callbacks = listeners.get(eventClass);
        if( callbacks == null) {
            callbacks = new ArrayList<>();
            listeners.put(eventClass, callbacks);
        }
        callbacks.add(new PrioritizedTreatEvent(EventTreatmentPriority.NORMAL, treatment));
        callbacks.sort(lambdasSorter);
    }

    void putListeners(Map<Class<?>, List<PrioritizedTreatEvent>> listeners) {
        for(Map.Entry<Class<?>, List<PrioritizedTreatEvent>> element : listeners.entrySet()) {
            List<PrioritizedTreatEvent> callbacks = this.listeners.get(element.getKey());
            if( callbacks == null) {
                callbacks = new ArrayList<>();
                this.listeners.put(element.getKey(), callbacks);
            }
            callbacks.addAll(element.getValue());
        }
    }
}
