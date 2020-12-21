package koh.patterns;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

public abstract class PatternProvider extends AbstractModule {

    protected final Injector parentInjector;

    public PatternProvider(Injector parentInjector) {
        this.parentInjector = parentInjector;
    }

}
