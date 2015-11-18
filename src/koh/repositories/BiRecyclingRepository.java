package koh.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class BiRecyclingRepository<K1, K2, T extends InUseCheckable> {

    private final Map<K1, RepositoryReference<T>> entitiesByFirstKey;
    private final Map<K2, RepositoryReference<T>> entitiesBySecondKey;

    private final ScheduledExecutorService scheduler;
    private final long ttl;
    private final TimeUnit ttlUnit;

    private final Function<T, K1> firstKeyResolver;
    private final Function<T, K2> secondKeyResolver;

    private final Function<K1, T> loaderByFirstKey;
    private final Function<K2, T> loaderBySecondKey;

    private Function<K1, K1> normalizeFirstKey;
    private Function<K2, K2> normalizeSecondKey;

    private final Consumer<T> unloader;

    public BiRecyclingRepository(Function<T, K1> firstKeyResolver, Function<T, K2> secondKeyResolver,
                                 Function<K1, T> loaderByFirstKey, Function<K2, T> loaderBySecondKey,
                                 Consumer<T> unloader, Function<K1, K1> normalizeFirstKey,
                                 Function<K2, K2> normalizeSecondKey, long ttl, TimeUnit ttlUnit) {

        this.entitiesByFirstKey = new ConcurrentHashMap<>();
        this.entitiesBySecondKey = new ConcurrentHashMap<>();

        this.firstKeyResolver = firstKeyResolver;
        this.secondKeyResolver = secondKeyResolver;

        this.loaderByFirstKey = loaderByFirstKey;
        this.loaderBySecondKey = loaderBySecondKey;

        this.normalizeFirstKey = normalizeFirstKey;
        this.normalizeSecondKey = normalizeSecondKey;

        this.unloader = unloader;

        this.ttl = ttl;
        this.ttlUnit = ttlUnit;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::doCleaning, ttl/2, ttl/2, ttlUnit);
    }

    private void doCleaning() {
        List<RepositoryReference<T>> copy = new ArrayList<>(entitiesByFirstKey.values());
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

    public T getByFirst(K1 key) {
        try {
            return this.getReferenceByFirst(key).get();
        }catch(NullPointerException ignored) {
            return null;
        }
    }

    public T getBySecond(K2 key) {
        try {
            return this.getReferenceBySecond(key).get();
        }catch(NullPointerException ignored) {
            return null;
        }
    }

    public RepositoryReference<T> getReferenceByFirst(K1 key) {
        final K1 finalKey = normalizeFirstKey.apply(key);
        try {
            RepositoryReference<T> value = entitiesByFirstKey.get(finalKey);
            if (value == null)
                return this.load(finalKey, null);

            value.sync(() -> {
                if (!value.loaded()) {
                    this.reload(value, finalKey, null);
                }
            });
            return value;
        }catch(NullPointerException ignored) {
            return null;
        }
    }

    public RepositoryReference<T> getReferenceBySecond(K2 key) {
        final K2 finalKey = normalizeSecondKey.apply(key);
        try {
            RepositoryReference<T> value = entitiesBySecondKey.get(finalKey);
            if(value == null)
                return this.load(null, finalKey);

            value.sync(() -> {
                if(!value.loaded()) {
                    this.reload(value, null, finalKey);
                }
            });
            return value;
        }catch(NullPointerException ignored) {
            return null;
        }
    }

    private synchronized RepositoryReference<T> load(K1 firstKey, K2 secondKey) {
        RepositoryReference<T> value;
        if(firstKey != null) {
            RepositoryReference<T> existing = entitiesByFirstKey.get(firstKey);
            if(existing != null)
                return existing;

            value = new RepositoryReference<>();
            value.set(loaderByFirstKey.apply(firstKey));
            secondKey = secondKeyResolver.apply(value.get());
        } else {
            RepositoryReference<T> existing = entitiesBySecondKey.get(secondKey);
            if(existing != null)
                return existing;

            value = new RepositoryReference<>();
            value.set(loaderBySecondKey.apply(secondKey));
            firstKey = firstKeyResolver.apply(value.get());
        }


        entitiesByFirstKey.put(normalizeFirstKey.apply(firstKey), value);
        entitiesBySecondKey.put(normalizeSecondKey.apply(secondKey), value);
        return value;
    }

    private RepositoryReference<T> reload(RepositoryReference<T> reference, K1 firstKey, K2 secondKey) {
        T reloaded;
        if(firstKey != null) {
            reloaded = loaderByFirstKey.apply(firstKey);
        } else {
            reloaded = loaderBySecondKey.apply(secondKey);
        }
        reference.set(reloaded);
        return reference;
    }

    public void dispose() {
        scheduler.shutdownNow();
    }

}
