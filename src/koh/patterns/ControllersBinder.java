package koh.patterns;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import koh.patterns.event.api.EventListener;
import koh.patterns.handler.api.Handler;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class ControllersBinder {

    private final String packageName;
    private final Injector parentInjector;

    public ControllersBinder(Injector parentInjector, String packageName) {
        this.parentInjector = parentInjector;
        this.packageName = packageName;
    }

    public Injector bind() {
        return parentInjector.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Reflections reflections = new Reflections(new ConfigurationBuilder()
                        .filterInputsBy(new FilterBuilder().includePackage(packageName))
                        .setUrls(ClasspathHelper.forPackage(packageName))
                        .setScanners(new SubTypesScanner()));

                reflections.getSubTypesOf(Controller.class).forEach((klass) -> bind(klass).asEagerSingleton());
            }
        });
    }
}
