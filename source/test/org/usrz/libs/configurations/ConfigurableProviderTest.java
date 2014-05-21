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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import javax.inject.Named;
import javax.inject.Qualifier;

import org.testng.annotations.Test;
import org.usrz.libs.testing.AbstractTest;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class ConfigurableProviderTest extends AbstractTest {

    @Test
    public void testUnannotated() {
        final String string = Guice.createInjector(new TestModule(),
                (binder) -> binder.bind(String.class).toProvider(TestProvider.class)
            ).getInstance(String.class);
        assertEquals(string, "unannotated");
    }

    @Test
    public void testUnannotatedInstance() {
        final String string = Guice.createInjector(new TestModule(),
                (binder) -> binder.bind(String.class).toProvider(new TestProvider())
            ).getInstance(String.class);
        assertEquals(string, "unannotated");
    }

    @Test
    public void testUnannotatedOverridden() {
        final String string = Guice.createInjector(new TestModule(),
                (binder) -> binder.bind(String.class).toProvider(new TestProvider().with(new ConfigurationsBuilder().put("foo", "override").build()))
            ).getInstance(String.class);
        assertEquals(string, "override");
    }

    @Test
    public void testAnnotationType() {
        final String string = Guice.createInjector(new TestModule(),
                (binder) -> binder.bind(String.class).toProvider(new TestProvider().with(TestAnnotation.class))
            ).getInstance(String.class);
        assertEquals(string, "withAnnotationType");
    }

    @Test
    public void testAnnotationTypeUnbound() {
        final String string = Guice.createInjector(new TestModule(),
                (binder) -> binder.bind(String.class).toProvider(new TestProvider().with(Named.class))
            ).getInstance(String.class);
        assertEquals(string, "unannotated");
    }

    @Test
    public void testAnnotation() {
        final String string = Guice.createInjector(new TestModule(),
                (binder) -> binder.bind(String.class).toProvider(new TestProvider().with("named"))
            ).getInstance(String.class);
        assertEquals(string, "withAnnotation");
    }

    @Test
    public void testAnnotationUnbound() {
        final String string = Guice.createInjector(new TestModule(),
                (binder) -> binder.bind(String.class).toProvider(new TestProvider().with("blah"))
            ).getInstance(String.class);
        assertEquals(string, "unannotated");
    }

    @Test
    public void testUnbound() {
        final String string = Guice.createInjector(
                (binder) -> binder.bind(String.class).toProvider(new TestProvider().with("blah"))
            ).getInstance(String.class);
        assertEquals(string, "defaultValue");
    }

    @Qualifier
    @Retention(RUNTIME)
    public static @interface TestAnnotation {}

    public static class TestModule implements Module {

        @Override
        public void configure(Binder binder) {
            binder.bind(Configurations.class).annotatedWith(Names.named("named")).toInstance(new ConfigurationsBuilder().put("foo", "withAnnotation").build());
            binder.bind(Configurations.class).annotatedWith(TestAnnotation.class).toInstance(new ConfigurationsBuilder().put("foo", "withAnnotationType").build());
            binder.bind(Configurations.class).toInstance(new ConfigurationsBuilder().put("foo", "unannotated").build());
        }
    }

    public static class TestProvider extends ConfigurableProvider<String, TestProvider> {

        @Override
        public String get(Injector injector, Configurations configurations) {
            return configurations.get("foo", "defaultValue");
        }

    }
}
