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

import java.io.Closeable;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.function.Supplier;

import javax.security.auth.Destroyable;

public final class Password implements Supplier<char[]>, Destroyable, Closeable {

    private static final SecureRandom random = new SecureRandom();
    private static final int hashCode = random.nextInt();

    private volatile boolean destroyed = false;
    private final Object lock = new Object();
    private final char[] password;

    public Password(char[] password) {
        if (password == null) throw new NullPointerException("Null password");
        if (password.length == 0) throw new IllegalArgumentException("Empty password");
        this.password = password;
    }

    @Override
    public char[] get() {
        synchronized (lock) {
            if (!destroyed) return password;
            throw new IllegalStateException("Password destroyed");
        }
    }

    @Override
    public void close() {
        synchronized (lock) {
            if (destroyed) return;

            /* First iteration, random characters */
            for (int x = 0; x < password.length; x ++) {
                password[x] = (char) random.nextInt();
            }
            /* Second iteration, nulls */
            Arrays.fill(password, '\0');
            destroyed = true;
        }
    }

    @Override
    @Deprecated
    public void destroy() {
        close();
    }

    @Override
    public boolean isDestroyed() {
        synchronized (lock) {
            return destroyed;
        }
    }

    /* ====================================================================== */

    @Override
    protected void finalize() {
        /* Make entirely and positively sure we wipe the memory */
        for (int x = 0; x < password.length; x ++) {
            password[x] = (char) random.nextInt();
        }
        Arrays.fill(password, '\0');
        destroyed = true;
    }

    @Override
    public int hashCode() {
        return Password.class.hashCode()
             ^ Arrays.hashCode(password)
             ^ hashCode;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (object == null) return false;
        if (destroyed == true) return false;
        try {
            final Password password = (Password) object;
            if (password.destroyed) return false;
            return Arrays.equals(this.password, password.password);
        } catch (ClassCastException exception) {
            return false;
        }
    }
}
