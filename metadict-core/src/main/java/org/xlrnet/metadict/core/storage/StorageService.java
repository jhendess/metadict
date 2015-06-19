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

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Main interface for accessing Metadict storage services. The storage service provides CRUD operations on a simple
 * key-value store with namespaces. Each key can be used exactly one time per namespace. When using CRUD operations,
 * all attributes of the objects will be (de-)serialized. Namespaces can be used for separating different types of
 * data.
 * <p>
 * When connecting to an external storage service, the stored data will be available globally for each connected
 * client. This may be helpful for implementing persistent data storage across multiple Metadict instances.
 */
public interface StorageService {

    /**
     * Store a new value of any type in the requested namespace with a given key. This method will throw a {@link
     * StorageException} if there is already an object with the same key under in the same namespace.
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
     *         The class to store. Its canonical class name should be used for internal indexes/namespaces of the
     *         backend.
     * @return The input object if it could be saved - never null. In case of any error situations, an exception will be
     * thrown.
     * @throws StorageException
     */
    @NotNull
    <T> T create(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageException;

    /**
     * Deletes the associated value for a given key in the given namespace. After deleting, the associated value will
     * not be accessible anymore by read or update operations.
     *
     * @param namespace
     *         The name of the namespace in which the key lies. Must be a non-empty and non-null string.
     * @param key
     *         The key for the new object. Must be a non-empty and non-null string.
     * @return True, if any value was deleted or false if none. A false return value indicates always that no value
     * could be found in the given namespace for the given key. In case of an internal backend error, an exception will
     * be thrown.
     * @throws StorageException
     *         Will be thrown if any backend errors occured.
     */
    boolean delete(@NotNull String namespace, @NotNull String key) throws StorageException;

    @NotNull
    <T> Optional<T> read(@NotNull String namespace, @NotNull String key, Class<T> clazz) throws StorageException;

    @NotNull
    <T> T update(@NotNull String namespace, @NotNull String key, @NotNull T newValue) throws StorageException;

}
