/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.xlrnet.metadict.core.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Helper class with static utility methods.
 */
public class CommonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    /** Valid naming pattern for storage services. */
    private static final Pattern STORAGE_SERVICE_NAME_PATTERN = Pattern.compile("[A-z][A-z0-9\\-]*");

    private CommonUtils() {

    }

    /**
     * Returns the value of a property in a given .properties-File or a default value.
     *
     * @param filename
     *         name of the properties file
     * @param propertyName
     *         the property name whose value should be returned
     * @param defaultValue
     *         the default value that should be used if the property couldn't be found
     * @return the property value
     */
    public static String getProperty(String filename, String propertyName, String defaultValue) {
        String result = defaultValue;
        Properties properties = new Properties();
        try (InputStream propertiesStream = CommonUtils.class.getClassLoader().getResourceAsStream(filename)) {
            if (propertiesStream != null) {
                properties.load(propertiesStream);
                result = properties.getProperty(propertyName);
            } else {
                LOGGER.error("File {} could not be found", filename);
            }
        } catch (IOException e) {
            LOGGER.warn("Unable to read property field {}", propertyName, e);
        }
        return result;
    }

    /**
     * Returns the value of a property in a given .properties-File.
     *
     * @param filename
     *         name of the properties file
     * @param propertyName
     *         the property name whose value should be returned
     * @return the property value or null if none could be found
     */
    public static String getProperty(String filename, String propertyName) {
        return getProperty(filename, propertyName, null);
    }

    /**
     * Returns all properties in a given .properties-File.
     *
     * @param filename
     *         name of the properties file
     * @return A map of all property values
     */
    public static Map<String, String> getProperties(String filename) {
        Map<String, String> propertyMap = new HashMap<>();
        Properties properties = new Properties();
        try (InputStream propertiesStream = CommonUtils.class.getClassLoader().getResourceAsStream(filename)) {
            if (propertiesStream != null) {
                properties.load(propertiesStream);

                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    propertyMap.put(entry.getKey().toString(), entry.getValue().toString());
                }

            } else {
                LOGGER.error("File {} could not be found", filename);
            }
        } catch (IOException e) {
            LOGGER.error("An I/O error occurred", e);
        }
        return propertyMap;
    }

    /**
     * Check if the given string is a valid identifier for a storage service. An identifier may contain only lower- and
     * uppercase letters, numbers and the dash symbol ("-"). Identifiers may only begin with letters and are always
     * handled case-sensitive.
     *
     * @param identifier The identifier to check
     * @return True if valid, otherwise false
     */
    public static boolean isValidStorageServiceName(@NotNull String identifier) {
        return STORAGE_SERVICE_NAME_PATTERN.matcher(identifier).matches();
    }
}
