package koh.patterns.services;

import com.google.common.base.Preconditions;
import koh.patterns.services.api.ServiceDependency;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
class ServiceDependencyImpl implements ServiceDependency {
    private final String value;
    private static final long serialVersionUID = 0L;

    public ServiceDependencyImpl(String value) {
        this.value = (String) Preconditions.checkNotNull(value, "name");
    }

    public String value() {
        return this.value;
    }

    public int hashCode() {
        return 127 * "value".hashCode() ^ this.value.hashCode();
    }

    public boolean equals(Object o) {
        if(!(o instanceof ServiceDependency)) {
            return false;
        } else {
            ServiceDependency other = (ServiceDependency)o;
            return this.value.equals(other.value());
        }
    }

    public String toString() {
        String var1 = String.valueOf(String.valueOf(ServiceDependency.class.getName()));
        String var2 = String.valueOf(String.valueOf(this.value));
        return (new StringBuilder(9 + var1.length() + var2.length())).append("@").append(var1).append("(value=").append(var2).append(")").toString();
    }

    public Class<? extends Annotation> annotationType() {
        return ServiceDependency.class;
    }
}
