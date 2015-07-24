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

import com.rits.cloning.Cloner;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.storage.*;

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
public class InMemoryStorage implements StorageEngine {

    private final static Logger LOGGER = LoggerFactory.getLogger(InMemoryStorage.class);

    private final MultiKeyMap backingMap = new MultiKeyMap();

    private Cloner cloner = Cloner.standard();

    /**
     * Count how many keys are currently registered in the specified namespace and return the result.
     * A key must be listed in the returned value after it was created and may not be shown anymore after it had been
     * deleted.
     *
     * @param namespace
     *         The name of the namespace in which the keys lie. Must be a non-empty and non-null string.
     * @return The number of how many keys are currently registered in the given namespace.
     * @throws StorageOperationException
     *         Will be thrown when trying to create a new object with an already used key.
     */
    @Override
    public long countKeysInNamespace(@NotNull String namespace) throws StorageBackendException {
        return 0;
    }

    /**
     * Count how many namespaces are currently registered in the store and return the result. The definition of  "in
     * the store" may differ between different implementations. In general, a namespace should be listed once there is
     * at least one key stored inside it.
     *
     * @return The number of how many namespaces are currently registered in the store.
     * @throws StorageOperationException
     *         Will be thrown when trying to create a new object with an already used key.
     */
    @Override
    public long countNamespaces() throws StorageBackendException {
        return 0;
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
        checkArguments(namespace, key);
        checkNotNull(value);

        if (backingMap.containsKey(namespace, key)) {
            LOGGER.debug("Creation failed: key {} exists already in namespace {}", key, namespace);
            throw new StorageOperationException("Creation failed: key already exists", namespace, key);
        }
        else {
            LOGGER.debug("Created key {} in namespace {}", key, namespace);
            backingMap.put(namespace, key, cloneValue(value));
        }

        return value;
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
        checkArguments(namespace, key);

        if (!backingMap.containsKey(namespace, key))
            return false;

        backingMap.remove(namespace, key);
        return true;
    }

    /**
     * Return an {@link Iterable} of Strings with all currently registered keys in a specified namespace. A key must be
     * listed in the returned value after it was created and may not be shown anymore after it had been deleted.
     *
     * @param namespace
     *         The name of the namespace in which the keys lie. Must be a non-empty and non-null string.
     * @return An {@link Iterable} of Strings with all currently registered keys in the specified namespace.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     */
    @Override
    public Iterable<String> listKeysInNamespace(@NotNull String namespace) {
        return null;
    }

    /**
     * Return an {@link Iterable} of Strings with all currently registered namespaces in the store. The definition of
     * "in the store" may differ between different implementations. In general, a namespace should be listed  once
     * there is at least one key stored inside it.
     *
     * @return An {@link Iterable} of Strings with all currently registered namespaces in the store.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     */
    @Override
    public Iterable<String> listNamespaces() throws StorageBackendException {
        return null;
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
        checkArguments(namespace, key);

        if (!backingMap.containsKey(namespace, key))
            return Optional.empty();

        T value = clazz.cast(backingMap.get(namespace, key));

        return Optional.of(cloneValue(value));
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
        checkArguments(namespace, key);
        checkNotNull(newValue);

        if (!backingMap.containsKey(namespace, key))
            throw new StorageOperationException("Update failed: key doesn't exist", namespace, key);

        this.backingMap.put(namespace, key, cloneValue(newValue));

        return newValue;
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
        // Do nothing
    }

    /**
     * Make sure that both namespace and key are neither null nor blank.
     */
    private void checkArguments(@NotNull String namespace, @NotNull String key) {
        checkArgument(StringUtils.isNotBlank(namespace), "Illegal namespace name");
        checkArgument(StringUtils.isNotBlank(key), "Illegal key name");
    }

    private <T extends Serializable> T cloneValue(@NotNull T value) {
        return cloner.deepClone(value);
    }

}
