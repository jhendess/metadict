/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakob Hendeß
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

package org.xlrnet.metadict.core.api.config;

import org.xlrnet.metadict.api.storage.StorageServiceProvider;

import java.util.Map;

/**
 * Configuration interface for the storage subsystem.
 */
public interface StorageConfiguration {

    /**
     * Returns the name of the default storage engine that should be used.
     *
     * @return the name of the default storage engine that should be used.
     */
    String getDefaultStorage();

    /**
     * Returns a map of storage engine-specific configurations. Each value inside this map represents a map of
     * configuration properties for a storage engine. The keys on the top-level must correspond to the returned values
     * of {@link StorageServiceProvider#getStorageBackendIdentifier()} .
     *
     * @return a map of storage engine-specific configurations.
     */
    Map<String, Map<String, String>> getEngineConfigurations();
}
