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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.storage.StorageBackendException;
import org.xlrnet.metadict.api.storage.StorageEngine;
import org.xlrnet.metadict.api.storage.StorageOperationException;

import java.io.Serializable;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Storage engine for accessing a MapDB backend. MapDB is a simple key-value store that can operate both in-memory and
 * on file-basis.
 *
 * Be aware that the current implementation does not use atomic transactions for creating and updating!
 */
public class MapdbStorageEngine implements StorageEngine {

    private final Logger LOGGER = LoggerFactory.getLogger(MapdbStorageEngine.class);

    private final DBMaker dbMaker;

    private final DB db;

    protected MapdbStorageEngine(DBMaker dbMaker) {
        this.dbMaker = dbMaker;
        this.db = dbMaker.make();
    }

    /**
     * Management method that will be called by the Metadict core when the storage should be disconnected. This may
     * happen e.g. when the core is being stopped or when a storage engine is being unloaded from the core.
     * <p>
     * After this method has been invoked, all successive method calls to the original engine will throw an {@link
     * StorageShutdownException}. Note, that this behaviour <i>must not</i> be implemented by the storage itself but is
     * provided through the core.
     */
    @Override
    public void shutdown() {
        LOGGER.info("Committing database changes ...");
        db.commit();
        LOGGER.info("Closing database ...");
        db.close();
    }

    /**
     * Store a new value of any type in the requested namespace with a given key. This method will throw a {@link
     * StorageBackendException} if there is already an object with the same key in the same namespace.
     * <p>
     * Upon saving, all private attributes will be extracted and stored in the storage backend. It depends on the
     * concrete backend implementation, whether the given class must be {@link Serializable}. However, it is recommended
     * to store only serializable class to avoid problems.
     *
     * @param namespace
     *         The name of the namespace in which the key should be placed. Must be a non-empty and non-null string.
     * @param key
     *         The key for the new object. Must be a non-empty and non-null string.
     * @param value
     *         The object that should be stored under the given key in the given namespace.
     * @return The input object if it could be saved - but never null. In case of any error situations, an exception
     * will be thrown.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     * @throws StorageOperationException
     *         Will be thrown when trying to create a new object with an already used key.
     */
    @NotNull
    @Override
    public <T extends Serializable> T create(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException, StorageOperationException {
        HTreeMap<Object, Object> namespaceMap = internalOpenNamespace(namespace);



        return null;
    }

    /**
     * Put a new value of any type in the requested namespace with a given key. This method does not need a previously
     * created key in the specified namespace, thus calling {@link #create(String, String, Serializable)} doesn't need
     * to be called before. Use {@link #create(String, String, Serializable)} if you want to make sure, that the new key
     * does not exist.
     * <p>
     * Upon saving, all private attributes will be extracted and stored in the storage backend. It depends on the
     * concrete backend implementation, whether the given class must be {@link Serializable}. However, it is recommended
     * to store only serializable class to avoid problems.
     *
     * @param namespace
     *         The name of the namespace in which the key should be placed. Must be a non-empty and non-null string.
     * @param key
     *         The key for the new object. Must be a non-empty and non-null string.
     * @param value
     *         The object that should be stored under the given key in the given namespace.
     * @return The input object if it could be saved - but never null. In case of any error situations, an exception
     * will be thrown.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     */
    @NotNull
    @Override
    public <T extends Serializable> T put(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException {
        return null;
    }

    /**
     * Checks if a value is associated with the specified key in the specified namespace. A key is defined as specified
     * if a value under the given key was either previously created with {@link #create(String, String, Serializable)}
     * or {@link #put(String, String, Serializable)} and has not been deleted with {@link #delete(String, String)}.
     *
     * @param namespace
     *         The name of the namespace in which the key should be placed. Must be a non-empty and non-null string.
     * @param key
     *         The key for the new object. Must be a non-empty and non-null string.
     * @return True if a value with the specified key exists, otherwise false.
     * @throws StorageBackendException
     */
    @Override
    public boolean containsKey(@NotNull String namespace, @NotNull String key) throws StorageBackendException {
        return false;
    }

    /**
     * Delete the associated value for a given key in the given namespace. After deleting, the associated value will not
     * be accessible anymore by read or update operations.
     *
     * @param namespace
     *         The name of the namespace in which the key lies. Must be a non-empty and non-null string.
     * @param key
     *         The key for the new object. Must be a non-empty and non-null string.
     * @return True, if any value was deleted or false if none. A false return value indicates always that no value
     * could be found in the given namespace for the given key. In case of an internal backend error, an exception will
     * be thrown.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     */
    @Override
    public boolean delete(@NotNull String namespace, @NotNull String key) throws StorageBackendException {
        return false;
    }

    /**
     * Return the stored value as an {@link Optional} behind a given key in the given namespace. If no stored value
     * could be found, the returned {@link Optional} object will be empty.
     * <p>
     * When reading a stored value, a new object will <strong>always</strong> be created - no caching mechanisms should
     * be used on the internal implementation.
     *
     * @param namespace
     *         The name of the namespace in which the key lies. Must be a non-empty and non-null string.
     * @param key
     *         The key for the new object. Must be a non-empty and non-null string.
     * @param clazz
     *         The target class to which the returned object should be casted. If casting fails, a {@link
     *         ClassCastException} might be thrown.
     * @return An {@link Optional} that contains the value behind the specified key. If no value could be found, the
     * returned optional object will be empty. If casting the stored object failed, a {@link ClassCastException} might
     * be thrown.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     * @throws ClassCastException
     *         Will be thrown if the found value cannot be casted to the required class.
     */
    @NotNull
    @Override
    public <T extends Serializable> Optional<T> read(@NotNull String namespace, @NotNull String key, Class<T> clazz) throws StorageBackendException, ClassCastException {
        return null;
    }

    /**
     * Update the stored value for a given key in a given namespace. Upon updating, the existing value will be replaced
     * completely. If there is no value associated with the given key, an exception will be thrown.
     *
     * @param namespace
     *         The name of the namespace in which the key lies. Must be a non-empty and non-null string.
     * @param key
     *         The key for the new object. Must be a non-empty and non-null string.
     * @param newValue
     *         The new value that should replace the existing one.
     * @return The input object if it could be saved - but never null. In case of any error situations, an exception
     * will be thrown.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     * @throws StorageOperationException
     *         Will be thrown when trying to update a non-existing key.
     */
    @NotNull
    @Override
    public <T extends Serializable> T update(@NotNull String namespace, @NotNull String key, @NotNull T newValue) throws StorageBackendException, StorageOperationException {
        return null;
    }

    /**
     * Create a new or open an existing map with the given namespace name.
     *
     * @param namespace
     *         The name of the namespace.
     * @return The internal map for accessing the namespace.
     */
    private HTreeMap<Object, Object> internalOpenNamespace(@NotNull String namespace) {
        return db.getHashMap(namespace);
    }

    /**
     * Make sure that both namespace and key are neither null nor blank.
     */
    private void checkArguments(@NotNull String namespace, @NotNull String key, @NotNull String value) {
        checkArgument(StringUtils.isNotBlank(namespace), "Illegal namespace name");
        checkArgument(StringUtils.isNotBlank(key), "Illegal key name");
        checkNotNull(value, "Value may not be null");
    }
}
