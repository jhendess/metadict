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

import java.io.Serializable;
import java.util.Optional;

/**
 * Main interface for accessing Metadict storage services. The storage service provides CRUD operations on a simple
 * key-value store with namespaces. Each key can be used exactly one time per namespace. When using CRUD operations, all
 * attributes of the objects will be (de-)serialized. Namespaces can be used for separating different types of data.
 * <p>
 * When connecting to an external storage service, the stored data will be available globally for each connected client.
 * This may be helpful for implementing persistent data storage across multiple Metadict instances.
 * <p>
 * Note, that this interface is only for accessing the storage. You have to implement {@link StorageEngine} interface,
 * which extends this interface.
 */
public interface StorageService {

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
    long countKeysInNamespace(@NotNull String namespace) throws StorageBackendException;

    /**
     * Count how many namespaces are currently registered in the store and return the result. The definition of  "in
     * the store" may differ between different implementations. In general, a namespace should be listed once there is
     * at least one key stored inside it.
     *
     * @return The number of how many namespaces are currently registered in the store.
     * @throws StorageOperationException
     *         Will be thrown when trying to create a new object with an already used key.
     */
    long countNamespaces() throws StorageBackendException;

    /**
     * Store a new value of any type in the requested namespace with a given key. This method will throw a {@link
     * StorageBackendException} if there is already an object with the same key in the same namespace.
     * <p>
     * Upon saving, all private attributes will be extracted and stored in the storage backend. It depends on the
     * concrete backend implementation, whether the given class must be {@link java.io.Serializable}. However, it is
     * recommended to store only serializable class to avoid problems.
     *
     * @param namespace
     *         The name of the namespace in which the key should be placed. Must be a non-empty and non-null string.
     * @param key
     *         The key for the new object. Must be a non-empty and non-null string.
     * @param value
     *         The object that should be stored under the given key in the given namespace.
     * @param <T>
     *         The class of the value to store - must be serializable.
     * @return The input object if it could be saved - but never null. In case of any error situations, an exception
     * will be thrown.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     * @throws StorageOperationException
     *         Will be thrown when trying to create a new object with an already used key.
     */
    @NotNull
    <T extends Serializable> T create(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException, StorageOperationException;

    /**
     * Put a new value of any type in the requested namespace with a given key. This method does not need a previously
     * created key in the specified namespace, thus calling {@link #create(String, String, Serializable)} doesn't need
     * to be called before. Use {@link #create(String, String, Serializable)} if you want to make sure, that the new key
     * does not exist.
     * <p>
     * Upon saving, all private attributes will be extracted and stored in the storage backend. It depends on the
     * concrete backend implementation, whether the given class must be {@link java.io.Serializable}. However, it is
     * recommended to store only serializable class to avoid problems.
     *
     * @param namespace
     *         The name of the namespace in which the key should be placed. Must be a non-empty and non-null string.
     * @param key
     *         The key for the new object. Must be a non-empty and non-null string.
     * @param value
     *         The object that should be stored under the given key in the given namespace.
     * @param <T>
     *         The class of the value to store - must be serializable.
     * @return The input object if it could be saved - but never null. In case of any error situations, an exception
     * will be thrown.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     */
    @NotNull
    <T extends Serializable> T put(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException;

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
    boolean containsKey(@NotNull String namespace, @NotNull String key) throws StorageBackendException;

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
    boolean delete(@NotNull String namespace, @NotNull String key) throws StorageBackendException;

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
    Iterable<String> listKeysInNamespace(@NotNull String namespace);

    /**
     * Return an {@link Iterable} of Strings with all currently registered namespaces in the store. The definition of
     * "in the store" may differ between different implementations. In general, a namespace should be listed  once
     * there is at least one key stored inside it.
     *
     * @return An {@link Iterable} of Strings with all currently registered namespaces in the store.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     */
    Iterable<String> listNamespaces() throws StorageBackendException;

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
     * @param <T>
     *         The type of the returned object - must be serializable.
     * @return An {@link Optional} that contains the value behind the specified key. If no value could be found, the
     * returned optional object will be empty. If casting the stored object failed, a {@link ClassCastException} might
     * be thrown.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     * @throws ClassCastException
     *         Will be thrown if the found value cannot be casted to the required class.
     */
    @NotNull
    <T extends Serializable> Optional<T> read(@NotNull String namespace, @NotNull String key, Class<T> clazz) throws StorageBackendException, ClassCastException;

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
     * @param <T>
     *         The class of the value to store - must be serializable.
     * @return The input object if it could be saved - but never null. In case of any error situations, an exception
     * will be thrown.
     * @throws StorageBackendException
     *         Will be thrown if any backend errors occurred.
     * @throws StorageOperationException
     *         Will be thrown when trying to update a non-existing key.
     */
    @NotNull
    <T extends Serializable> T update(@NotNull String namespace, @NotNull String key, @NotNull T newValue) throws StorageBackendException, StorageOperationException;

}
