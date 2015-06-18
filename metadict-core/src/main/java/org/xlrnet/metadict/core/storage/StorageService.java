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
 * key-value store. Each key may be used one time for each class.
 * <p>
 * E.g.: There may be only one value with key "123" for the Class "Sample". However you can save another value with
 * key "123" for the Class "Test". The canonical class name should be used for internal indexes/namespaces.
 */
public interface StorageService {

    /**
     * Store a new value of any type under a given key. The targeted index
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     * @throws IllegalArgumentException
     */
    @NotNull
    <T> T create(@NotNull String key, @NotNull T value) throws IllegalArgumentException;

    @NotNull
    <T> Optional<T> read(@NotNull String key, Class<T> clazz) throws IllegalArgumentException;

    @NotNull
    <T> T update(@NotNull String key, @NotNull T newValue) throws IllegalArgumentException;

    @NotNull
    <T> T delete(String key) throws IllegalArgumentException;

}
