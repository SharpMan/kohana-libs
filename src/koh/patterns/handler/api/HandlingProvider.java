package koh.patterns.handler.api;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import koh.patterns.PatternProvider;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public abstract class HandlingProvider<E> extends PatternProvider {

    protected final String packageName;
    protected final Class<E> emitterClass;

    public HandlingProvider(Injector parentInjector, String packageName, Class<E> emitterClass) {
        super(parentInjector);

        this.packageName = packageName;
        this.emitterClass = emitterClass;
    }

}
