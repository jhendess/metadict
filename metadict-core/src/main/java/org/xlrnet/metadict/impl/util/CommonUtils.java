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

package org.xlrnet.metadict.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Helper class with static utility methods.
 */
public class CommonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * Returns the value of a property in a given .properties-File
     *
     * @param filename
     *         name of the properties file
     * @param propertyName
     *         the property name whose value should be returned
     * @return the property value
     */
    public static String getProperty(String filename, String propertyName) {
        String result = null;
        Properties properties = new Properties();
        try (InputStream propertiesStream = CommonUtils.class.getClassLoader().getResourceAsStream(filename)) {
            if (propertiesStream != null) {
                properties.load(propertiesStream);
                result = properties.getProperty(propertyName);
            } else {
                LOGGER.error("File {} could not be found", filename);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to read property field {} ({}: {})", propertyName, e.getClass().getSimpleName(), e.getMessage());
        }
        return result;
    }

}
