package koh.patterns.handler;

import com.google.inject.Injector;
import com.google.inject.Scopes;
import koh.patterns.handler.api.ConsumableHandleMethod;
import koh.patterns.handler.api.Handler;
import koh.patterns.handler.api.HandlerEmitter;
import koh.patterns.handler.api.HandlingProvider;
import koh.patterns.handler.context.RequireContexts;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Usable for procedures handling like @[attribute] public void onHelloConnect(NetClient client, HelloConnectMessage msg);
 *
 * @param <E> emitter
 * @param <S> source
 */
public class ConsumerHandlingProvider<E extends HandlerEmitter, S> extends HandlingProvider<E> {

    private final Class<? extends Annotation> attribute;
    private final Class<S> rootParameterClass;
    private final ConsumerHandlerExecutor<E, S> toProvide;

    public ConsumerHandlingProvider(ConsumerHandlerExecutor<E, S> toProvide, Injector parentInjector, String packageName, Class<E> rootEmitterclass,
                                    Class<? extends Annotation> attribute, Class<S> rootParameterClass) {
        super(parentInjector, packageName, rootEmitterclass);
        this.toProvide = toProvide;
        this.attribute = attribute;
        this.rootParameterClass = rootParameterClass;
    }

    @Override
    protected void configure() {
        requestStaticInjection(emitterClass);

        Map<Class<?>, List<ConsumableHandleMethod<E, S>>> handlers = new HashMap<>();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new TypeAnnotationsScanner(), new MethodAnnotationsScanner()));

        final Map<Class, RequireContexts> requirements = new HashMap<>();
        reflections.getTypesAnnotatedWith(RequireContexts.class).stream().forEach((klass) -> {
            requirements.put(klass, klass.getAnnotation(RequireContexts.class));
        });

        reflections.getMethodsAnnotatedWith(attribute).stream().forEach((method) -> {
            if(method.getDeclaringClass() == Handler.class
                    || !Handler.class.isAssignableFrom(method.getDeclaringClass()))
                return;
            if(!Modifier.isPublic(method.getModifiers()))
                return;
            if(method.getParameterTypes().length == 2
                    && emitterClass == method.getParameterTypes()[0]
                    && method.getParameterTypes()[1] != rootParameterClass
                    && rootParameterClass.isAssignableFrom(method.getParameterTypes()[1])) {
                List<ConsumableHandleMethod<E, S>> callbacks = handlers.get(method.getParameterTypes()[1]);
                if( callbacks == null) {
                    callbacks = new ArrayList<>();
                    handlers.put(method.getParameterTypes()[1], callbacks);
                }
                if(!Modifier.isStatic(method.getModifiers())) {
                    if (parentInjector.getBinding(method.getDeclaringClass()) == null)
                        bind(method.getDeclaringClass()).in(Scopes.SINGLETON);
                    callbacks.add(new ConsumerMethodInvoker(parentInjector.getInstance(method.getDeclaringClass()),
                            requirements.get(method.getDeclaringClass()), method)::call);
                }
            }
        });

        toProvide.putHandlers(handlers);
    }
}
