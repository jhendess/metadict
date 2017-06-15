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

package org.xlrnet.metadict.core.services.storage;

import com.rits.cloning.Cloner;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.storage.StorageBackendException;
import org.xlrnet.metadict.api.storage.StorageOperationException;
import org.xlrnet.metadict.api.storage.StorageService;

import java.io.Serializable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * In-memory implementation of a {@link StorageService}. All stored data will only be stored in-memory and not on an
 * external backend. Thus all stored data will be lost upon destroying the service object.
 * <p>
 * The internal implementation is based on a MultiKeyMap.
 */
public class InMemoryStorage implements StorageService {

    private final static Logger LOGGER = LoggerFactory.getLogger(InMemoryStorage.class);

    private final MultiKeyMap backingMap = new MultiKeyMap();

    private Cloner cloner = Cloner.standard();

    @Override
    public long countKeysInNamespace(@NotNull String namespace) throws StorageBackendException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long countNamespaces() throws StorageBackendException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public <T extends Serializable> T create(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException, StorageOperationException {
        checkArguments(namespace, key);
        checkNotNull(value);

        if (this.backingMap.containsKey(namespace, key)) {
            LOGGER.debug("Creation failed: key {} exists already in namespace {}", key, namespace);
            throw new StorageOperationException("Creation failed: key already exists", namespace, key);
        } else {
            LOGGER.trace("Created key {} in namespace {}", key, namespace);
            this.backingMap.put(namespace, key, cloneValue(value));
        }

        return value;
    }

    @NotNull
    @Override
    public <T extends Serializable> T put(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException {
        checkArguments(namespace, key);
        checkNotNull(value);

        LOGGER.trace("Put namespace={}, key={}, value={}", namespace, key, value);
        this.backingMap.put(namespace, key, cloneValue(value));

        return value;
    }

    @Override
    public boolean containsKey(@NotNull String namespace, @NotNull String key) throws StorageBackendException {
        checkArguments(namespace, key);
        return this.backingMap.containsKey(namespace, key);
    }

    @Override
    public boolean delete(@NotNull String namespace, @NotNull String key) throws StorageBackendException {
        checkArguments(namespace, key);

        LOGGER.trace("Deleting namespace={}, key={}", namespace, key);

        if (!this.backingMap.containsKey(namespace, key)) {
            return false;
        }

        this.backingMap.remove(namespace, key);
        return true;
    }

    @Override
    public Iterable<String> listKeysInNamespace(@NotNull String namespace) {
        return null;
    }

    @Override
    public Iterable<String> listNamespaces() throws StorageBackendException {
        throw new UnsupportedOperationException("listNamespaces() is not supported");
    }

    @NotNull
    @Override
    public <T extends Serializable> Optional<T> read(@NotNull String namespace, @NotNull String key, Class<T> clazz) throws StorageBackendException, StorageOperationException {
        checkArguments(namespace, key);

        if (!this.backingMap.containsKey(namespace, key)) {
            return Optional.empty();
        }

        T value = clazz.cast(this.backingMap.get(namespace, key));

        return Optional.of(cloneValue(value));
    }

    @NotNull
    @Override
    public <T extends Serializable> T update(@NotNull String namespace, @NotNull String key, @NotNull T newValue) throws StorageBackendException, StorageOperationException {
        checkArguments(namespace, key);
        checkNotNull(newValue);

        if (!this.backingMap.containsKey(namespace, key)) {
            throw new StorageOperationException("Update failed: key doesn't exist", namespace, key);
        }

        this.backingMap.put(namespace, key, cloneValue(newValue));

        return newValue;
    }

    /**
     * Resets all content in the inmemory storage. This method should never be called publicly.
     */
    public void reset() {
        backingMap.clear();
    }

    /**
     * Make sure that both namespace and key are neither null nor blank.
     */
    private void checkArguments(@NotNull String namespace, @NotNull String key) {
        checkArgument(StringUtils.isNotBlank(namespace), "Illegal namespace name");
        checkArgument(StringUtils.isNotBlank(key), "Illegal key name");
    }

    private <T extends Serializable> T cloneValue(@NotNull T value) {
        return this.cloner.deepClone(value);
    }

}
