package koh.patterns.handler.api;

import com.google.inject.Injector;
import koh.patterns.PatternProvider;

public abstract class HandlingProvider<E> extends PatternProvider {

    protected final String packageName;
    protected final Class<E> rootEmitterclass;

    public HandlingProvider(Injector parentInjector, String packageName, Class<E> rootEmitterclass) {
        super(parentInjector);
        this.packageName = packageName;
        this.rootEmitterclass = rootEmitterclass;
    }

}
