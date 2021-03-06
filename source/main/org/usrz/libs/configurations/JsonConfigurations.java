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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A {@link Configurations} implementation reading <em>key-value</em> mappings
 * from <em><a href="http://json.org/">JSON</a>-like file</em>.
 *
 * @author <a href="mailto:pier@usrz.com">Pier Fumagalli</a>
 */
public class JsonConfigurations extends MappedConfigurations {

    /* Platform-dependant line separator to wrap our JSON */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /* Our charset, UTF8, always */
    private static final Charset UTF8 = Charset.forName("UTF8");

    /* ====================================================================== */
    /* CONSTRUCTION                                                           */
    /* ====================================================================== */

    /**
     * Create a new {@link JsonConfigurations} instance reading a
     * <em><a href="http://json.org/">JSON</a>-like file</em> from the
     * specified {@link Reader}.
     */
    public JsonConfigurations(Reader reader)
    throws IOException, ConfigurationsException {
        super(parse(reader));
    }

    /**
     * Create a new {@link JsonConfigurations} instance reading a
     * <em><a href="http://json.org/">JSON</a>-like file</em> from the
     * specified {@link InputStream}.
     */
    public JsonConfigurations(InputStream input)
    throws IOException, ConfigurationsException {
        super(parse(input));
    }

    /* ====================================================================== */

    private static final Map<String, Object> parse(InputStream input)
    throws IOException, ConfigurationsException {
        if (input == null) throw new NullPointerException("Null input stream");
        return parse(new InputStreamReader(input, UTF8));
    }

    @SuppressWarnings("unchecked")
    private static final Map<String, Object> parse(Reader reader)
    throws IOException, ConfigurationsException {
        if (reader == null) throw new NullPointerException("Null reader");

        /* Read our JSON fully, wrapping it in a { json } structure */
        final StringBuilder builder = new StringBuilder("callback.invoke(").append(LINE_SEPARATOR);

        final char[] buffer = new char[4096];
        int read = -1;
        while ((read = reader.read(buffer)) >= 0) {
            if (read > 0) builder.append(buffer, 0, read);
        }

        builder.append(");");

        /* We can use the built-in JavaScript interpreter to parse this JSON */
        try {
            final ScriptEngineManager manager = new ScriptEngineManager();
            final ScriptEngine engine = manager.getEngineByMimeType("application/javascript");

            engine.getContext().setAttribute("callback", Callback.CALLBACK, ScriptContext.ENGINE_SCOPE);

            return (Map<String, Object>) engine.eval(builder.toString());

        } catch (RuntimeException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof ConfigurationsException)
                throw (ConfigurationsException) cause;
            throw exception;

        } catch (ScriptException exception) {
            /* Wrap a JsonParseException in a ConfigurationsException */
            throw new ConfigurationsException("Unable to parse JSON format", exception);
        }
    }

    /* ====================================================================== */
    /* CALLED BY JAVASCRIPT TO BUILD OUR CONFIGURATIONS INSTANCE              */
    /* ====================================================================== */

    public static class Callback {

        private static final Callback CALLBACK = new Callback();

        public Map<String, Object> invoke(Map<?, ?> map) {
            return build(map, new HashMap<>(), "");
        }

        private Map<String, Object> build(Map<?, ?> from, Map<String, Object> to, String prefix) {
            for (Object object: from.keySet()) {
                final String key = prefix + object.toString();
                final Object value = from.get(object);
                if (value instanceof Map) {
                    build((Map<?, ?>) value, to, key + ".");
                } else {
                    to.put(key, value);
                }
            }
            return to;
        }
    }

}
