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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeConverterBinding;

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
implements Provider<T> {

    private static final Key<Configurations> KEY = Key.get(Configurations.class);

    @SuppressWarnings("unchecked")
    private final P thisInstance = (P) this;

    protected final Configurations configurations = new DelegateConfigurations();
    protected final Injector injector = new DelegateInjector();

    private Configurations configured = EMPTY_CONFIGURATIONS;
    private Key<Configurations> key;
    private Injector injected;

    /* ====================================================================== */

    /**
     * Create a new {@link ConfigurableProvider} instance.
     */
    protected ConfigurableProvider() {
        this.key = KEY;
    }

    /* ====================================================================== */

    /**
     * Specify and override anything from the {@link Injector} the actual
     * {@link Configurations} instance to use.
     */
    public final P with(Configurations configurations) {
        if (configurations == null) throw new NullPointerException("Null Configurations");
        this.configured = configurations;
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

    /* ====================================================================== */

    @Inject
    private void setInjector(Injector injector) {
        if (key != null) this.configured = getConfigurations(injector);
        this.injected = injector;
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

    private class DelegateInjector implements Injector {


        @Override
        public void injectMembers(Object instance) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            injected.injectMembers(instance);
        }

        @Override
        public <X> MembersInjector<X> getMembersInjector(TypeLiteral<X> typeLiteral) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getMembersInjector(typeLiteral);
        }

        @Override
        public <X> MembersInjector<X> getMembersInjector(Class<X> type) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getMembersInjector(type);
        }

        @Override
        public Map<Key<?>, Binding<?>> getBindings() {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getBindings();
        }

        @Override
        public Map<Key<?>, Binding<?>> getAllBindings() {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getAllBindings();
        }

        @Override
        public <X> Binding<X> getBinding(Key<X> key) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getBinding(key);
        }

        @Override
        public <X> Binding<X> getBinding(Class<X> type) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getBinding(type);
        }

        @Override
        public <X> Binding<X> getExistingBinding(Key<X> key) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getExistingBinding(key);
        }

        @Override
        public <X> List<Binding<X>> findBindingsByType(TypeLiteral<X> type) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.findBindingsByType(type);
        }

        @Override
        public <X> Provider<X> getProvider(Key<X> key) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getProvider(key);
        }

        @Override
        public <X> Provider<X> getProvider(Class<X> type) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getProvider(type);
        }

        @Override
        public <X> X getInstance(Key<X> key) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getInstance(key);
        }

        @Override
        public <X> X getInstance(Class<X> type) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getInstance(type);
        }

        @Override
        public Injector getParent() {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getParent();
        }

        @Override
        public Injector createChildInjector(Iterable<? extends Module> modules) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.createChildInjector(modules);
        }

        @Override
        public Injector createChildInjector(Module... modules) {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.createChildInjector(modules);
        }

        @Override
        public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getScopeBindings();
        }

        @Override
        public Set<TypeConverterBinding> getTypeConverterBindings() {
            if (injected != null) throw new IllegalStateException("No Injector available");
            return injected.getTypeConverterBindings();
        }
    }

    /* ====================================================================== */

    private class DelegateConfigurations extends Configurations {

        @Override
        public String getString(Object key, String defaultValue) {
            return configured == null ? defaultValue : configured.getString(key, defaultValue);
        }

        @Override
        public Set<java.util.Map.Entry<String, String>> entrySet() {
            return configured == null ? Collections.emptySet() : configured.entrySet();
        }

        @Override
        public int size() {
            return configured == null ? 0 : configured.size();
        }

    }
}
