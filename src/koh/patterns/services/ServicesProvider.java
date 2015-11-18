package koh.patterns.services;

import com.google.inject.*;
import koh.patterns.PatternProvider;
import koh.patterns.services.api.DependsOn;
import koh.patterns.services.api.Service;
import koh.patterns.services.api.ServiceDependency;

import java.util.*;
import java.util.stream.Stream;

public class ServicesProvider extends PatternProvider {

    public static ServiceDependency inGroup(String groupName) {
        return new ServiceDependencyImpl(groupName);
    }

    private static final Comparator<Class<? extends Service>> ordered = (left, right) -> {
        Stream<Class<? extends Service>> leftDependencies = Stream.empty();
        DependsOn leftAnnot = left.getAnnotation(DependsOn.class);
        if(leftAnnot != null)
            leftDependencies = Stream.of(leftAnnot.value());

        return leftDependencies.parallel()
                .anyMatch((dependency) -> dependency == right)
                ? (1) : (-1);
    };

    private static final Comparator<Service> reversed = (o1, o2) -> -1;

    private final List<Class<? extends Service>> servicesClasses;
    private final List<Service> instances;
    private final String groupName;
    private final ServiceDependency marker;

    private boolean started = false;

    @SafeVarargs
    public ServicesProvider(String groupName, Injector parentInjector, Class<? extends Service>... servicesClasses) {
        super(parentInjector);

        this.groupName = groupName;
        this.marker = new ServiceDependencyImpl(groupName);
        this.servicesClasses = new ArrayList<>(servicesClasses.length);
        this.instances = new ArrayList<>(servicesClasses.length);
        Collections.addAll(this.servicesClasses, servicesClasses);
        this.servicesClasses.sort(ordered);
        this.createShutdownHook(this::stop);
    }

    private void createShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    @Override
    protected void configure() {
        servicesClasses.stream().forEach(this::bindService);
    }

    private Injector injector = parentInjector;

    @SuppressWarnings({"unchecked"})
    private void bindService(Class<? extends Service> serviceClass) {
        Service service = injector.getInstance(serviceClass);

        bind((Class)serviceClass).annotatedWith(marker).toInstance(service);
        injector = injector.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind((Class)serviceClass).annotatedWith(marker).toInstance(service);
                service.configure(binder());
                service.inject(injector);
            }
        });
        instances.add(service);
    }

    public void start() {
        if(started)
            return;
        try {
            instances.stream()
                    .forEach(Service::start);
        }finally {
            this.started = true;
        }
    }

    public void stop() {
        if(!started)
            return;
        try {
            instances.stream().sorted(reversed)
                    .forEach(Service::stop);
        } finally {
            this.started = false;
        }
    }

    @Override
    public String toString() {
        return "ServicesProvider." + groupName + servicesClasses.toString();
    }
}
