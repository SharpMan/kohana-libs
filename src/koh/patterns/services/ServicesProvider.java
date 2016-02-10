package koh.patterns.services;

import com.google.common.collect.Lists;
import com.google.inject.*;
import koh.patterns.services.api.DependsOn;
import koh.patterns.services.api.Service;
import koh.patterns.services.api.ServiceDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public class ServicesProvider extends AbstractModule {

    public static ServiceDependency inGroup(String groupName) {
        return new ServiceDependencyImpl(groupName);
    }

    private static final Comparator<Service> ordered = (leftService, rightService) -> {
        Class<? extends Service> left = leftService.getClass();
        Class<? extends Service> right = rightService.getClass();

        Stream<Class<? extends Service>> leftDependencies = null;
        Stream<Class<? extends Service>> rightDependencies = null;

        DependsOn annot = left.getAnnotation(DependsOn.class);
        if(annot != null)
            leftDependencies = Stream.of(annot.value()).parallel();

        annot = right.getAnnotation(DependsOn.class);
        if(annot != null)
            rightDependencies = Stream.of(annot.value()).parallel();

        if(leftDependencies == null && rightDependencies == null)
            return 0;

        if(leftDependencies != null && rightDependencies == null)
            return leftDependencies
                    .anyMatch((dependency) -> dependency.equals(right)) ? (1) : (-1);
        else if(leftDependencies == null)
            return rightDependencies
                    .anyMatch((dependency) -> dependency.equals(left)) ? (-1) : (1);
        else {
            boolean leftDependsOnRight = leftDependencies
                    .anyMatch((dependency) -> dependency.equals(right));

            boolean rightDependsOnLeft = rightDependencies
                    .anyMatch((dependency) -> dependency.equals(left));

            if(leftDependsOnRight && rightDependsOnLeft)
                throw new IllegalStateException("Cyclic dependency between " + left.getName() + " and " + right.getName());

            return rightDependsOnLeft ? (-1) : (1);
        }
    };

    private static final Comparator<Service> reversed = (o1, o2) -> -1 * ordered.compare(o1, o2);

    private final ArrayList<Service> instances;
    private final String groupName;
    private final ServiceDependency marker;

    private boolean started = false;

    private final Logger logger;

    public ServicesProvider(String groupName, Service... services) {
        this.groupName = groupName;
        this.logger = LoggerFactory.getLogger(groupName);
        this.marker = new ServiceDependencyImpl(groupName);
        this.instances = Lists.newArrayList(services);
        this.sortServices();
        this.createShutdownHook(this::stop);
    }

    private void sortServices() {
        this.instances.sort((o1, o2) -> {
            Class<? extends Service> left = o1.getClass();
            Class<? extends Service> right = o2.getClass();

            int leftDependencies = 0;
            int rightDependencies = 0;

            DependsOn annot = left.getAnnotation(DependsOn.class);
            if(annot != null)
                leftDependencies = annot.value().length;

            annot = right.getAnnotation(DependsOn.class);
            if(annot != null)
                rightDependencies = annot.value().length;

            if(rightDependencies == leftDependencies)
                return 0;

            return leftDependencies > rightDependencies ? 1 : -1;
        });
        this.instances.sort(ordered);
    }

    private void createShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread("Shutdown-"+groupName) {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    @Override
    protected void configure() {
        instances.stream().forEachOrdered(this::bindService);
    }

    @SuppressWarnings({"unchecked"})
    private void bindService(Service service) {
        bind((Class)service.getClass()).annotatedWith(marker).toInstance(service);
        service.configure(this.binder());
    }

    public void start(Injector app) {
        if(started)
            return;
        try {
            logger.info("Starting services ...");
            long time = System.currentTimeMillis();
            instances.stream()
                    .forEachOrdered((service) -> {
                        try {
                            app.injectMembers(service);
                            service.inject(app);
                            service.start();
                            logger.info("Service " + service.getClass().getSimpleName() + " started");
                        }catch(Exception e) {
                            logger.error(e.getMessage(), e);
                            System.exit(1);
                        }
                    });
            logger.info("Services started in {} ms !", (System.currentTimeMillis() - time));
        }finally {
            this.started = true;
        }
    }

    private void stop() {
        if(!started)
            return;
        try {
            logger.info("Stopping services ...");
            instances.stream().sorted(reversed)
                    .forEachOrdered((service) -> {
                        try {
                            service.stop();
                            logger.info("Service " + service.getClass().getSimpleName() + " stopped");
                        }catch(Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    });
        } finally {
            this.started = false;
        }
    }

    @Override
    public String toString() {
        return "ServicesProvider." + groupName;
    }
}
