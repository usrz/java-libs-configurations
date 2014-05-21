/* ========================================================================== *
 * Copyright 2014 USRZ.com and Pier Paolo Fumagalli                           *
 * -------------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 *  http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * ========================================================================== */
package org.usrz.libs.configurations;

import static org.usrz.libs.configurations.Configurations.EMPTY_CONFIGURATIONS;

import java.lang.annotation.Annotation;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Qualifier;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * A simple provider which can be <em>configured</em>
 * <p>
 * By default, the {@link Configurations} {@linkplain #configurations accessible
 * from this} are gathered from an {@link Injector} keyed without any
 * {@link Qualifier} {@link Annotation}.
 *
 * @author <a href="mailto:pier@usrz.com">Pier Fumagalli</a>
 * @param <T> The type of instances managed by this {@link Provider}.
 * @param <P> The actual type of this provider.
 */
@SuppressWarnings("restriction")
public abstract class ConfigurableProvider<T, P extends ConfigurableProvider<T, P>>
implements javax.inject.Provider<T>,
           com.google.inject.Provider<T> {

    private static final Key<Configurations> KEY = Key.get(Configurations.class);

    @SuppressWarnings("unchecked")
    private final P thisInstance = (P) this;
    private final boolean singleton;

    private Configurations configurations = EMPTY_CONFIGURATIONS;
    private Key<Configurations> key;
    private Injector injector;
    private T instance;

    /* ====================================================================== */

    /**
     * Create a new {@link ConfigurableProvider} instance.
     */
    protected ConfigurableProvider() {
        this(true);
    }

    /**
     * Create a new {@link ConfigurableProvider} instance.
     */
    protected ConfigurableProvider(boolean singleton) {
        this.singleton = singleton;
        this.key = KEY;
    }

    /* ====================================================================== */

    /**
     * Specify and override anything from the {@link Injector} the actual
     * {@link Configurations} instance to use.
     */
    public final P with(Configurations configurations) {
        if (configurations == null) throw new NullPointerException("Null Configurations");
        this.configurations = configurations;
        this.key = null;
        return thisInstance;
    }

    /**
     * Specify the name of a {@link Named} qualifying the {@link Configurations}
     * instance to retrieve from the {@link Injector}.
     */
    public final P with(String name) {
        if (name == null) throw new NullPointerException("Null name");
        return this.with(Names.named(name));
    }

    /**
     * Specify the {@link Annotation} qualifying the {@link Configurations}
     * instance to retrieve from the {@link Injector}.
     */
    public final P with(Annotation annotation) {
        if (annotation == null) throw new NullPointerException("Null annotation");
        if (key == null) throw new IllegalStateException("Configurations already set up");
        key = Key.get(Configurations.class, annotation);
        return thisInstance;
    }

    /**
     * Specify the {@link Annotation} type qualifying the {@link Configurations}
     * instance to retrieve from the {@link Injector}.
     */
    public final P with(Class<? extends Annotation> annotationType) {
        if (annotationType == null) throw new NullPointerException("Null annotation type");
        if (key == null) throw new IllegalStateException("Configurations already set up");
        key = Key.get(Configurations.class, annotationType);
        return thisInstance;
    }

    /**
     * Return a {@link Key} of the specified <em>type</em> with the same
     * qualifier attributes configured to retrieve the {@link Configurations}
     * for this instance.
     */
    protected final <X> Key<X> key(Class<X> type) {
        return this.key == null ? Key.get(type) : this.key.ofType(type);
    }

    /**
     * Return a {@link Key} of the specified <em>type</em> with the same
     * qualifier attributes configured to retrieve the {@link Configurations}
     * for this instance.
     */
    protected final <X> Key<X> key(TypeLiteral<X> type) {
        return this.key == null ? Key.get(type) : this.key.ofType(type);
    }

    /* ====================================================================== */

    @javax.inject.Inject @com.google.inject.Inject
    private void setInjector(Injector injector) {
        if (key != null) this.configurations = getConfigurations(injector);
        this.injector = injector;
    }

    private Configurations getConfigurations(Injector injector) {

        /* Fully annotated? */
        Binding<Configurations> binding = injector.getExistingBinding(key);
        if (binding != null) return injector.getInstance(binding.getKey());

        /* Try to look up without attributes */
        if (key.hasAttributes()) {
            binding = injector.getExistingBinding(key.withoutAttributes());
            if (binding != null) return injector.getInstance(binding.getKey());
        }

        /* Do we have an annotation type? */
        if (key.getAnnotationType() != null) {
            binding = injector.getExistingBinding(Key.get(key.getTypeLiteral()));
            if (binding != null) return injector.getInstance(binding.getKey());
        }

        /* Nothing found */
        return EMPTY_CONFIGURATIONS;
    }

    /* ====================================================================== */

    @Override
    public final T get() {
        if (instance != null) return instance;

        if (injector == null) throw new ProvisionException("Injector not available in " + this.getClass().getName());
        try {
            final T instance = this.get(injector, configurations);
            if (instance == null) {
                throw new ProvisionException("Null instance returned by " + this.getClass().getName());
            } else if (singleton) {
                this.instance = instance;
            }
            return instance;
        } catch (Exception exception) {
            throw new ProvisionException("Exception providing instance in " + this.getClass().getName(), exception);
        }
    }

    /**
     * Create an instance of the object to provide.
     */
    protected abstract T get(Injector injector, Configurations configurations)
    throws Exception;

}
