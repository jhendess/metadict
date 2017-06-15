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

package org.xlrnet.metadict.storage.mapdb;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
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
 * Storage engine for accessing a MapDB backend. MapDB is a simple key-value store that can operate both in-memory and
 * on file-basis.
 * <p>
 * Be aware that the current implementation does not use atomic transactions for creating and updating!
 */
public class MapdbStorageEngine implements StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapdbStorageEngine.class);

    private final DBMaker dbMaker;

    private final Serializer[] serializers;

    private final DB db;

    protected MapdbStorageEngine(DBMaker dbMaker, Serializer... serializers) {
        this.dbMaker = dbMaker;
        this.serializers = serializers;
        this.db = dbMaker.make();
    }

    @Override
    public long countKeysInNamespace(@NotNull String namespace) throws StorageBackendException {
        return internalOpenNamespace(namespace).sizeLong();
    }

    @Override
    public long countNamespaces() throws StorageBackendException {
        return this.db.getAll().size();
    }

    @NotNull
    @Override
    public <T extends Serializable> T create(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException, StorageOperationException {
        checkArguments(namespace, key, value);

        HTreeMap<String, Object> namespaceMap = internalOpenNamespace(namespace);
        LOGGER.trace("Create namespace={}, key={}, value={}", namespace, key, value);

        if (namespaceMap.containsKey(key)) {
            LOGGER.debug("Creation failed: key {} exists already in namespace {}", key, namespace);
            throw new StorageOperationException("Creation failed: key already exists", namespace, key);
        } else {
            LOGGER.debug("Created key {} in namespace {}", key, namespace);
            namespaceMap.put(key, value);
        }

        return value;
    }

    @NotNull
    @Override
    public <T extends Serializable> T put(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException {
        checkArguments(namespace, key, value);

        HTreeMap<String, Object> openNamespace = internalOpenNamespace(namespace);

        LOGGER.trace("Putting namespace={}, key={}", namespace, key);
        openNamespace.put(key, value);

        return value;
    }

    @Override
    public boolean containsKey(@NotNull String namespace, @NotNull String key) throws StorageBackendException {
        checkArguments(namespace, key);

        return internalOpenNamespace(namespace).containsKey(key);
    }

    @Override
    public boolean delete(@NotNull String namespace, @NotNull String key) throws StorageBackendException {
        checkArguments(namespace, key);

        HTreeMap<String, Object> openNamespace = internalOpenNamespace(namespace);
        LOGGER.trace("Deleting namespace={}, key={}", namespace, key);
        boolean removeSuccessful = openNamespace.remove(namespace, key);

        if (!removeSuccessful) {
            LOGGER.trace("Failed to delete namespace={}, key={}", namespace, key);
        }

        return removeSuccessful;
    }

    @Override
    public Iterable<String> listKeysInNamespace(@NotNull String namespace) {
        checkArgument(StringUtils.isNotBlank(namespace), "Illegal namespace name");

        return internalOpenNamespace(namespace).keySet();
    }

    @Override
    public Iterable<String> listNamespaces() throws StorageBackendException {
        return this.db.getAll().keySet();
    }

    @NotNull
    @Override
    public <T extends Serializable> Optional<T> read(@NotNull String namespace, @NotNull String key, Class<T> clazz) throws StorageBackendException, StorageOperationException {
        checkArguments(namespace, key);

        HTreeMap<String, Object> namespaceMap = internalOpenNamespace(namespace);

        LOGGER.trace("Reading namespace={}, key={}", namespace, key);

        try {
            T readValue = clazz.cast(namespaceMap.get(key));
            return Optional.ofNullable(readValue);
        } catch (ClassCastException e) {
            throw new StorageOperationException("Reading value failed due to an invalid class cast", namespace, key, e);
        }
    }

    @NotNull
    @Override
    public <T extends Serializable> T update(@NotNull String namespace, @NotNull String key, @NotNull T newValue) throws StorageBackendException, StorageOperationException {
        checkArguments(namespace, key, newValue);

        HTreeMap<String, Object> namespaceMap = internalOpenNamespace(namespace);

        LOGGER.trace("Put namespace={}, key={}, value={}", namespace, key, newValue);

        if (!namespaceMap.containsKey(key)) {
            throw new StorageOperationException("Update failed: key doesn't exist", namespace, key);
        }

        namespaceMap.put(key, newValue);

        return newValue;
    }

    void shutdown() {
        commit();
        LOGGER.info("Closing database ...");
        this.db.close();
    }

    void commit() {
        LOGGER.debug("Committing database changes ...");
        this.db.commit();
    }

    /**
     * Create a new or open an existing map with the given namespace name.
     *
     * @param namespace
     *         The name of the namespace.
     * @return The internal map for accessing the namespace.
     */
    private HTreeMap<String, Object> internalOpenNamespace(@NotNull String namespace) {
        if (this.db.exists(namespace)) {
            return this.db.getHashMap(namespace);
        }
        LOGGER.trace("Creating new namespace={}", namespace);
        DB.HTreeMapMaker mapMaker = this.db.createHashMap(namespace);

        for (Serializer serializer : this.serializers) {
            mapMaker.valueSerializer(serializer);
        }

        return mapMaker.make();
    }

    /**
     * Make sure that both namespace and key are neither null nor blank and the value is not null.
     */
    private void checkArguments(@NotNull String namespace, @NotNull String key, @NotNull Object value) {
        checkArguments(namespace, key);
        checkNotNull(value, "Value may not be null");
    }

    /**
     * Make sure that both namespace and key are neither null nor blank.
     */
    private void checkArguments(@NotNull String namespace, @NotNull String key) {
        checkArgument(StringUtils.isNotBlank(namespace), "Illegal namespace name");
        checkArgument(StringUtils.isNotBlank(key), "Illegal key name");
    }
}
