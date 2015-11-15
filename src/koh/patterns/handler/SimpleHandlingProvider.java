package koh.patterns.handler;

import com.google.inject.Injector;
import com.google.inject.Scopes;
import koh.patterns.handler.api.HandleMethod;
import koh.patterns.handler.api.Handler;
import koh.patterns.handler.api.HandlerEmitter;
import koh.patterns.handler.api.HandlingProvider;
import koh.patterns.handler.context.RequireContexts;
import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Usable for procedures handling like @Connect public void onConnect(NetClient client);
 * @param <E>
 */
public class SimpleHandlingProvider<E extends HandlerEmitter> extends HandlingProvider<E> {

    private final SimpleHandlerExecutor<E> toProvide;

    public SimpleHandlingProvider(SimpleHandlerExecutor<E> toProvide, Injector parentInjector, String packageName, Class<E> rootEmitterclass) {
        super(parentInjector, packageName, rootEmitterclass);
        this.toProvide = toProvide;
    }

    @Override
    protected void configure() {

        requestStaticInjection(emitterClass);

        Map<Class<? extends Annotation>, List<HandleMethod<E>>> handlers = new HashMap<>();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new TypeAnnotationsScanner(), new MethodParameterScanner()));

        final Map<Class, RequireContexts> requirements = new HashMap<>();
        reflections.getTypesAnnotatedWith(RequireContexts.class).stream().forEach((klass) -> {
            requirements.put(klass, klass.getAnnotation(RequireContexts.class));
        });

        reflections.getMethodsReturn(void.class).stream().forEach((method) -> {
            if(method.getDeclaringClass() == Handler.class
                    || !Handler.class.isAssignableFrom(method.getDeclaringClass()))
                return;
            if(!Modifier.isPublic(method.getModifiers()))
                return;
            if(method.getParameterTypes().length == 1
                    && emitterClass == method.getParameterTypes()[0]) {

                for(Annotation annotation : method.getAnnotations()) {
                    List<HandleMethod<E>> callbacks = handlers.get(annotation.annotationType());
                    if( callbacks == null) {
                        callbacks = new ArrayList<>();
                        handlers.put(annotation.annotationType(), callbacks);
                    }
                    if(!Modifier.isStatic(method.getModifiers())) {
                        if (parentInjector.getBinding(method.getDeclaringClass()) == null)
                            bind(method.getDeclaringClass()).in(Scopes.SINGLETON);
                        callbacks.add(new SimpleMethodInvoker(parentInjector.getInstance(method.getDeclaringClass()),
                                requirements.get(method.getDeclaringClass()), method)::call);
                    }
                }
            }
        });

        toProvide.putHandlers(handlers);
    }
}
