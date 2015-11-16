package koh.patterns.services.api;

import com.google.inject.Binder;
import com.google.inject.Injector;

public interface Service {

    void start();

    void stop();

    default void configure(Binder binder){ };

    default void inject(Injector injector){ };
}
