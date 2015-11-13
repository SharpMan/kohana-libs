package koh.patterns.services.api;

import com.google.inject.Binder;

public interface Service {

    void start();

    void stop();

    default void configure(Binder binder){ };
}
