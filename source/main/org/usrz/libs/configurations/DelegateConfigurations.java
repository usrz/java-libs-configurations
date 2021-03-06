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

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DelegateConfigurations extends Configurations {

    private final Configurations configurations;

    protected DelegateConfigurations(Configurations configurations) {
        this.configurations = Objects.requireNonNull(configurations, "Null configurations");
    }

    @Override
    protected Configurations wrap(Map<?, ?> map) {
        return configurations.wrap(map);
    }

    @Override
    public boolean containsKey(Object key) {
        return configurations.containsKey(key);
    }

    @Override
    public String getString(Object key, String defaultValue) {
        return configurations.getString(key, defaultValue);
    }

    @Override
    public Password getPassword(Object key) {
        return configurations.getPassword(key);
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return configurations.entrySet();
    }

    @Override
    public int size() {
        return configurations.size();
    }

}
