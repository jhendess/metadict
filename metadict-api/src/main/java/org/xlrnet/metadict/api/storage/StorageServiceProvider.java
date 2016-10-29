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

package org.xlrnet.metadict.api.storage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Main interface for configuring and creating new {@link StorageService} instances. A class implementing this interface
 * must return a unique identifier for its supplied storage backend and be able to create and configure a new {@link
 * StorageService}.
 * <p>
 * All available implementations on the classpath will be detected and registered automatically by the Metadict core.
 */
public interface StorageServiceProvider {

    /**
     * Return the identifier of the supplied backend. An identifier may contain only lower- and uppercase letters,
     * numbers and the dash symbol ("-"). Identifiers may only begin with letters and are always handled
     * case-sensitive.
     * <p>
     * If multiple implementations with the same identifier have been found, the Metadict core will refuse to boot.
     *
     * @return the identifier of the backend.
     */
    String getStorageBackendIdentifier();


    /**
     * Return a {@link StorageDescription} object that contains descriptive i.e. textual information about and listeners
     * for the underlying engine. Textual information can be e.g.the name, url, etc. of the engine.
     *
     * @return an object that contains descriptive i.e. textual information about the underlying engine.
     */
    @NotNull
    StorageDescription getStorageDescription();

    /**
     * Create a new instance of the internal {@link StorageService}. The returned object must be configured according to
     * the parameters in the supplied map. The given map is a immutable map of key-value pairs taken from the
     * storage.properties file. It contains all values that begin with "storage." followed by the identifier returned by
     * {@link #getStorageBackendIdentifier()} followed by another dot. The keys inside the map contain only the the part
     * <i>after</i> this last dot.
     * <p>
     * Example: When the identifier of this storage backend is "example", the property "storage.example.someKey" would
     * be available as "someKey" in the map.
     *
     * @param configuration
     *         An immutable map of configuration parameters. See method description for more information.
     * @return A new instance of the internal {@link StorageService} that must be configured according to the supplied
     * map.
     */
    StorageService createNewStorageService(Map<String, String> configuration) throws StorageBackendException;

}
