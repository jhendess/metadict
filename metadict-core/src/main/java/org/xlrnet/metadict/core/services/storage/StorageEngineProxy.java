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

    /** The storage engine that will be proxied by this instance. */
    @NotNull
    protected final StorageService proxiedEngine;

    /** The attached listeners to this proxy. */
    @NotNull
    protected final List<ListenerConfiguration<StorageEventType, StorageEventListener>> listeners;

    /** Indicator whether this engine has already been shutdown. */
    private boolean shutdown = false;

    /** Active timers on this storage engine. */
    private final List<Timer> timers = new ArrayList<>();

    /**
     * Check if this engine is already shut down.
     *
     * @return True if shut down, otherwise false.
     */
    protected boolean isShutdown() {
        return this.shutdown;
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
        for (ListenerConfiguration<StorageEventType, StorageEventListener> listener : this.listeners) {
            if (StorageEventType.MAINTENANCE.equals(listener.getEventType())) {
                long intervalMilliseconds = listener.getIntervalSeconds() * 1000;
                final StorageEventListener eventListener = listener.getEventListener();
                Timer timer = new Timer();
                EventTimerTask<StorageService> timerTask = new EventTimerTask<>(StorageEventType.MAINTENANCE, eventListener, this.proxiedEngine);
                timer.scheduleAtFixedRate(timerTask, intervalMilliseconds, intervalMilliseconds);
                this.timers.add(timer);
                LOGGER.debug("Installed timer in {} milliseconds interval on engine '{}'", intervalMilliseconds, this.proxiedEngine.getClass().getCanonicalName());
            }
        }
    }

    @Override
    public long countKeysInNamespace(@NotNull String namespace) throws StorageBackendException {
        checkInternalState();

        try {
            return this.proxiedEngine.countKeysInNamespace(namespace);
        } catch (RuntimeException e) {
            throw new StorageBackendException(e);
        }
    }

    @Override
    public long countNamespaces() throws StorageBackendException {
        checkInternalState();

        try {
            return this.proxiedEngine.countNamespaces();
        } catch (RuntimeException e) {
            throw new StorageBackendException(e);
        }
    }

    @NotNull
    @Override
    public <T extends Serializable> T create(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException, StorageOperationException {
        checkInternalState();

        try {
            return this.proxiedEngine.create(namespace, key, value);
        } catch (RuntimeException e) {
            throw new StorageBackendException(e);
        }
    }

    @NotNull
    @Override
    public <T extends Serializable> T put(@NotNull String namespace, @NotNull String key, @NotNull T value) throws StorageBackendException {
        checkInternalState();

        try {
            return this.proxiedEngine.put(namespace, key, value);
        } catch (RuntimeException e) {
            throw new StorageBackendException(e);
        }
    }

    @Override
    public boolean containsKey(@NotNull String namespace, @NotNull String key) throws StorageBackendException {
        checkInternalState();

        try {
            return this.proxiedEngine.containsKey(namespace, key);
        } catch (RuntimeException e) {
            throw new StorageBackendException(e);
        }
    }

    @Override
    public boolean delete(@NotNull String namespace, @NotNull String key) throws StorageBackendException {
        checkInternalState();

        try {
            return this.proxiedEngine.delete(namespace, key);
        } catch (RuntimeException e) {
            throw new StorageBackendException(e);
        }
    }

    @Override
    public Iterable<String> listKeysInNamespace(@NotNull String namespace) throws StorageBackendException {
        checkInternalState();

        try {
            return this.proxiedEngine.listKeysInNamespace(namespace);
        } catch (RuntimeException e) {
            throw new StorageBackendException(e);
        }
    }

    @Override
    public Iterable<String> listNamespaces() throws StorageBackendException {
        checkInternalState();

        try {
            return this.proxiedEngine.listNamespaces();
        } catch (RuntimeException e) {
            throw new StorageBackendException(e);
        }
    }

    @NotNull
    @Override
    public <T extends Serializable> Optional<T> read(@NotNull String namespace, @NotNull String key, Class<T> clazz) throws StorageBackendException, StorageOperationException {
        checkInternalState();

        try {
            return this.proxiedEngine.read(namespace, key, clazz);
        } catch (RuntimeException e) {
            throw new StorageBackendException(e);
        }
    }

    @NotNull
    @Override
    public <T extends Serializable> T update(@NotNull String namespace, @NotNull String key, @NotNull T newValue) throws StorageBackendException, StorageOperationException {
        checkInternalState();
        return this.proxiedEngine.update(namespace, key, newValue);
    }

    /**
     * Management method that will be called by the Metadict core when the storage should be disconnected. This may
     * happen e.g. when the core is being stopped or when a storage engine is being unloaded from the core.
     * <p>
     * After this method has been invoked, all successive method calls to the original engine will throw an {@link
     * StorageShutdownException}. Note, that this behaviour <i>must not</i> be implemented by the storage itself but is
     * provided through the core.
     */
    void shutdown() {
        try {
            checkInternalState();
        } catch (StorageShutdownException e) {
            LOGGER.warn("Shutdown was already called while trying to shutdown storage engine {}", this.proxiedEngine.getClass().getCanonicalName());
        }
        LOGGER.info("Shutting down storage engine {} ...", this.proxiedEngine.getClass().getCanonicalName());
        stopTimers();

        notifyListeners(StorageEventType.SHUTDOWN);
        this.shutdown = true;
        LOGGER.info("Storage engine {} has been shut down", this.proxiedEngine.getClass().getCanonicalName());
    }

    private void stopTimers() {
        // TODO: Move to MessageBus
        LOGGER.debug("Stopping attached timers ...");
        for (Timer timer : this.timers) {
            timer.cancel();
        }
    }

    void notifyListeners(StorageEventType eventType) {
        // TODO: Move to MessageBus
        LOGGER.debug("Firing '{}' event to listeners", eventType);
        for (ListenerConfiguration<StorageEventType, StorageEventListener> listenerConfiguration : this.listeners) {
            try {
                if (eventType.equals(listenerConfiguration.getEventType())) {
                    listenerConfiguration.getEventListener().handleEvent(this.proxiedEngine);
                }
            } catch (Exception e) {
                LOGGER.error("Event handler threw an exception", e);
            }
        }
    }

    private void checkInternalState() throws StorageShutdownException {
        if (this.shutdown) {
            throw new StorageShutdownException();
        }
    }
}
