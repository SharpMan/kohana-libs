package koh.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class RecyclingRepository<K, T extends InUseCheckable> {

    private final Map<K, RepositoryReference<T>> entities;
    private final ScheduledExecutorService scheduler;
    private final long ttl;
    private final TimeUnit ttlUnit;

    private final Function<T, K> keyResolver;
    private final Function<K, T> loader;
    private final BiConsumer<K, T> unloader;

    public RecyclingRepository(Function<T, K> keyResolver, Function<K, T> loader, BiConsumer<K, T> unloader, long ttl, TimeUnit ttlUnit) {
        this.keyResolver = keyResolver;
        this.loader = loader;
        this.unloader = unloader;
        this.entities = new ConcurrentHashMap<>();

        this.ttl = ttl;
        this.ttlUnit = ttlUnit;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::doCleaning, ttl/2, ttl/2, ttlUnit);
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
        K key = keyResolver.apply(value);

        unloader.accept(key, value);
        reference.unset();
    }

    public T get(K key) {
        return this.getReference(key).get();
    }

    public RepositoryReference<T> getReference(K key) {
        RepositoryReference<T> value = entities.get(key);
        if(value == null)
            return this.load(key);

        value.sync(() -> {
            if(!value.loaded()) {
                this.reload(value, key);
            }
        });
        return value;
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
