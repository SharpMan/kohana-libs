package koh.patterns.event;

import com.google.inject.Injector;
import com.google.inject.Scopes;
import koh.patterns.event.api.EventListener;
import koh.patterns.event.api.EventTreatmentPriority;
import koh.patterns.event.api.Listen;
import koh.patterns.handler.api.HandlingProvider;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListeningProvider extends HandlingProvider<Event> {

    private final PrioritizedTreatEventComparator lambdasSorter = new PrioritizedTreatEventComparator();
    private final EventExecutor toProvide;

    public EventListeningProvider(EventExecutor toProvide, Injector parentInjector, String packageName) {
        super(parentInjector, packageName, Event.class);
        this.toProvide = toProvide;
    }

    @Override
    protected void configure() {
        Map<Class<?>, List<PrioritizedTreatEvent>> listeners = new HashMap<>();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new MethodAnnotationsScanner()));

        reflections.getMethodsAnnotatedWith(Listen.class).stream().forEach((method) -> {
            if(method.getDeclaringClass() == koh.patterns.event.api.EventListener.class
                    || !koh.patterns.event.api.EventListener.class.isAssignableFrom(method.getDeclaringClass()))
                return;
            if(!Modifier.isPublic(method.getModifiers()))
                return;
            if(method.getParameterTypes().length == 1
                    && method.getParameterTypes()[0] != emitterClass
                    && emitterClass.isAssignableFrom(method.getParameterTypes()[0])) {
                EventTreatmentPriority priority = method.getAnnotation(Listen.class).priority();
                List<PrioritizedTreatEvent> callbacks = listeners.get(method.getParameterTypes()[0]);
                if( callbacks == null) {
                    callbacks = new ArrayList<>();
                    listeners.put(method.getParameterTypes()[0], callbacks);
                }
                if(!Modifier.isStatic(method.getModifiers())) {
                    callbacks.add(new PrioritizedTreatEvent(priority, new EventMethodInvoker(
                            parentInjector.getInstance(method.getDeclaringClass()), method)::call));
                }
            }
        });

        for(List<PrioritizedTreatEvent> prioritized : listeners.values())
            prioritized.sort(lambdasSorter);

        toProvide.putListeners(listeners);
    }
}
