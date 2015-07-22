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

package org.xlrnet.metadict.core.storage;

import org.xlrnet.metadict.api.storage.StorageEngine;
import org.xlrnet.metadict.api.storage.StorageService;
import org.xlrnet.metadict.api.storage.StorageServiceProvider;

import java.util.Map;

/**
 * Storage service provider for creating a new instance of {@link InMemoryStorage}.
 */
public class InMemoryStorageProvider implements StorageServiceProvider {

    private static final String STORAGE_NAME = "inmemory";

    /**
     * Return the identifier of the supplied backend. An identifier may contain only lower- and uppercase letters,
     * numbers and the dash symbol ("-"). Identifiers may only begin with letters and are always handled
     * case-sensitive.
     * <p>
     * If multiple implementations with the same identifier have been found, the Metadict core will refuse to boot.
     *
     * @return the identifier of the backend.
     */
    @Override
    public String getStorageBackendIdentifier() {
        return STORAGE_NAME;
    }

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
    @Override
    public StorageEngine createNewStorageService(Map<String, String> configuration) {
        return new InMemoryStorage();
    }
}
