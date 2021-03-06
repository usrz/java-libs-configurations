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
import java.net.URL;
import java.util.Properties;

import org.usrz.libs.logging.Log;

/**
 * A {@link Configurations} implementation parsing mappings from a
 * {@link URL}, either in <em>Java {@linkplain Properties properties}</em>
 * or <em><a href="http://json.org/">JSON</a></em> format.
 *
 * <p>This class does not make any attempt to <em>discover</em> the format
 * of the contents to be parsed, it simply relies on the <em>extension</em>
 * which <b>must be</b> "<code>.json</code>" for JSON files, or either
 * "<code>.properties</code>" or "<code>.xml</code>" for properties files.</p>
 */
public class URLConfigurations extends DelegateConfigurations {

    private static final Log log = new Log();

    /**
     * Create a new {@link Configurations} instance parsing the specified
     * {@link URL}, either in <em>Java {@linkplain Properties properties}</em>
     * or <em><a href="http://json.org/">JSON</a></em> format.
     */
    public URLConfigurations(URL url)
    throws ConfigurationsException {
        super(load(url));
    }

    /* ====================================================================== */

    private static final Configurations load(URL url)
    throws ConfigurationsException {
        if (url == null) throw new NullPointerException("Null URL");

        log.debug("Parsing configurations from URL %s", url);

        final String name = url.toString();
        final String file = url.getFile();
        try {
            final InputStream input = url.openStream();
                try {
                if (file.endsWith(".json"))
                    return new JsonConfigurations(input);
                else if (file.endsWith(".properties") || name.endsWith(".xml")) {
                    return new PropertiesConfigurations(input);
                } else {
                    throw new IllegalArgumentException("URL \"" + name + "\" must end with \".json\", \".properties\", or \".xml\"");
                }
            } finally {
                input.close();
            }
        } catch (IOException exception) {
            throw new ConfigurationsException("I/O error loading URL " + url, exception);
        } catch (Exception exception) {
            throw new ConfigurationsException("Error loading URL " + url, exception);
        }
    }

}
