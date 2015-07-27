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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.event.ListenerConfiguration;
import org.xlrnet.metadict.api.storage.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wrapper class for controlling storage engine behaviour from {@link StorageServiceFactory}.
 */
class StorageEngineProxy implements StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageEngineProxy.class);

    @NotNull
    protected final StorageService proxiedEngine;

    @NotNull
    protected final List<ListenerConfiguration<StorageEventType, StorageEventListener>> listeners;

    private boolean shutdown = false;

    private final List<Timer> timers = new ArrayList<>();

    protected boolean isShutdown() {
        return shutdown;
    }

    protected StorageEngineProxy(@NotNull StorageService storageEngine, @NotNull List<ListenerConfiguration<StorageEventType, StorageEventListener>> listeners) {
        checkNotNull(storageEngine, "Storage engine may not be null");
        checkNotNull(listeners, "Listeners may not be null");

        this.listeners = listeners;
        this.proxiedEngine = storageEngine;

        installMaintenanceListeners();
    }

    private void installMaintenanceListeners() {
        // TODO: Move to MessageBus
        for (ListenerConfiguration<StorageEventType, StorageEventListener> listener : listeners) {
            if (StorageEventType.MAINTENANCE.equals(listener.getEventType())) {
                long intervalMilliseconds = listener.getIntervalSeconds() * 1000;
                final StorageEventListener eventListener = listener.getEventListener();
                Timer timer = new Timer();
                EventTimerTask<StorageService> timerTask = new EventTimerTask<>(StorageEventType.MAINTENANCE, eventListener, proxiedEngine);
                timer.scheduleAtFixedRate(timerTask, intervalMilliseconds, intervalMilliseconds);
                timers.add(timer);
                LOGGER.debug("Installed timer in {} milliseconds interval on engine '{}'", intervalMilliseconds, proxiedEngine.getClass().getCanonicalName());
            }
        }
    }

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
        checkInternalState();
        return proxiedEngine.countKeysInNamespace(namespace);
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
        checkInternalState();
        return proxiedEngine.countNamespaces();
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
        checkInternalState();
        return proxiedEngine.create(namespace, key, value);
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
        checkInternalState();
        return proxiedEngine.put(namespace, key, value);
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
        checkInternalState();
        return proxiedEngine.containsKey(namespace, key);
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
        checkInternalState();
        return proxiedEngine.delete(namespace, key);
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
        checkInternalState();
        return proxiedEngine.listKeysInNamespace(namespace);
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
        checkInternalState();
        return proxiedEngine.listNamespaces();
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
        checkInternalState();
        return proxiedEngine.read(namespace, key, clazz);
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
        checkInternalState();
        return proxiedEngine.update(namespace, key, newValue);
    }

    /**
     * Management method that will be called by the Metadict core when the storage should be disconnected. This may
     * happen e.g. when the core is being stopped or when a storage engine is being unloaded from the core.
     * <p>
     * After this method has been invoked, all successive method calls to the original engine will throw an {@link
     * StorageShutdownException}. Note, that this behaviour <i>must not</i> be implemented by the storage itself but is
     * provided through the core.
     */
    public void shutdown() {
        checkInternalState();
        LOGGER.info("Shutting down storage engine {} ...", proxiedEngine.getClass().getCanonicalName());
        stopTimers();

        notifyListeners(StorageEventType.SHUTDOWN);
        shutdown = true;
        LOGGER.info("Storage engine {} has been shut down", proxiedEngine.getClass().getCanonicalName());
    }

    private void stopTimers() {
        // TODO: Move to MessageBus
        LOGGER.debug("Stopping attached timers ...");
        for (Timer timer : timers) {
            timer.cancel();
        }
    }

    protected void notifyListeners(StorageEventType eventType) {
        // TODO: Move to MessageBus
        LOGGER.debug("Firing '{}' event to listeners", eventType);
        for (ListenerConfiguration<StorageEventType, StorageEventListener> listenerConfiguration : listeners) {
            try {
                if (eventType.equals(listenerConfiguration.getEventType())) {
                    listenerConfiguration.getEventListener().handleEvent(proxiedEngine);
                }
            } catch (Exception e) {
                LOGGER.error("Event handler threw an exception", e);
            }
        }
    }

    private void checkInternalState() {
        if (shutdown)
            throw new StorageShutdownException();
    }
}
