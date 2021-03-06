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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.usrz.libs.logging.Log;

/**
 * A {@link Configurations} implementation parsing mappings from a
 * {@link File}, either in <em>Java {@linkplain Properties properties}</em>
 * or <em><a href="http://json.org/">JSON</a></em> format.
 *
 * <p>This class does not make any attempt to <em>discover</em> the format
 * of the contents to be parsed, it simply relies on the <em>extension</em>
 * which <b>must be</b> "<code>.json</code>" for JSON files, or either
 * "<code>.properties</code>" or "<code>.xml</code>" for properties files.</p>
 */
public class FileConfigurations extends DelegateConfigurations {

    private static final Log log = new Log();

    /**
     * Create a new {@link Configurations} instance parsing the specified
     * {@link File}, either in <em>Java {@linkplain Properties properties}</em>
     * or <em><a href="http://json.org/">JSON</a></em> format.
     */
    public FileConfigurations(File file)
    throws ConfigurationsException {
        super(load(file));
    }

    /* ====================================================================== */

    private static final Configurations load(File file)
    throws ConfigurationsException {
        if (file == null) throw new NullPointerException("Null URL");

        log.debug("Parsing configurations from file %s", file);

        final String name = file.getAbsolutePath();
        if (!file.isFile())
            throw new IllegalArgumentException("File " + name + " not found (or not a file)");

        try {
            final FileInputStream input = new FileInputStream(file);
            try {
                if (name.endsWith(".json") || name.endsWith(".js"))
                    return new JsonConfigurations(input);
                else if (name.endsWith(".properties") || name.endsWith(".xml")) {
                    return new PropertiesConfigurations(input);
                } else {
                    throw new IllegalArgumentException("Invalid file extension for \"" + name + "\"");
                }
            } finally {
                input.close();
            }
        } catch (IOException exception) {
            throw new ConfigurationsException("I/O error reading file " + file, exception);
        } catch (Exception exception) {
            throw new ConfigurationsException("Error reading file " + file, exception);
        }
    }

}
