package koh.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;

import java.util.function.Consumer;

public class LambdaModule extends AbstractModule {

    private final Consumer<Binder> configure;

    public LambdaModule(Consumer<Binder> configure) {
        this.configure = configure;
    }

    @Override
    protected void configure() {
        configure.accept(this.binder());
    }
    
}
