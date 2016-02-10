package koh.repositories;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class RecyclingRepository<K, T extends InUseCheckable> {

    private final Map<K, RepositoryReference<T>> entities;
    private final ScheduledExecutorService scheduler;
    private final long ttl;
    private final TimeUnit ttlUnit;

    private final Function<K, T> loader;
    private final Consumer<T> unloader;

    public RecyclingRepository(Function<K, T> loader, Consumer<T> unloader, long ttl, TimeUnit ttlUnit) {
        this.loader = loader;
        this.unloader = unloader;
        this.entities = new ConcurrentHashMap<>();

        this.ttl = ttl;
        this.ttlUnit = ttlUnit;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::doCleaning, ttl/2, ttl/2, ttlUnit);
    }

    public Collection<RepositoryReference<T>> values() {
        return Collections.unmodifiableCollection(entities.values());
    }

    private void doCleaning() {
        List<RepositoryReference<T>> copy = new ArrayList<>(entities.values());
        for(RepositoryReference<T> reference : copy) {
            try{
                reference.sync(() -> this.recycle(reference));
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void recycle(RepositoryReference<T> reference) {
        if(!reference.loaded() || reference.get().inUse()
                || !reference.accessedAfter(ttlUnit.toMillis(ttl)))
            return;

        T value = reference.get();

        unloader.accept(value);
        reference.unset();
    }

    public RepositoryReference<T> getLoaded(K key) {
        RepositoryReference<T> ref = entities.get(key);
        return (ref != null && ref.loaded()) ? ref : null;
    }

    public T get(K key) {
        try {
            return this.getReference(key).get();
        }catch(NullPointerException ignored) {
            return null;
        }
    }

    public RepositoryReference<T> getReference(K key) {
        try {
            RepositoryReference<T> value = entities.get(key);
            if(value == null)
                return this.load(key);

            value.sync(() -> {
                if(!value.loaded())
                    this.reload(value, key);
                else
                    value.reused();
            });
            return value;
        }catch(NullPointerException ignored) {
            return null;
        }
    }

    private synchronized RepositoryReference<T> load(K key) {
        RepositoryReference<T> existing = entities.get(key);
        if(existing != null)
            return existing;

        RepositoryReference<T> value = new RepositoryReference<>();
        value.set(loader.apply(key));
        entities.put(key, value);
        return value;
    }

    private RepositoryReference<T> reload(RepositoryReference<T> reference, K key) {
        T reloaded = loader.apply(key);
        reference.set(reloaded);
        return reference;
    }

    public void dispose() {
        scheduler.shutdownNow();
    }

}
