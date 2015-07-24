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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.exception.MetadictRuntimeException;
import org.xlrnet.metadict.api.storage.StorageEngine;
import org.xlrnet.metadict.api.storage.StorageService;
import org.xlrnet.metadict.api.storage.StorageServiceProvider;
import org.xlrnet.metadict.core.util.CommonUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for accessing and creating new {@link StorageService} instances. This factory can be used in two ways: either
 * use its CDI {@link javax.enterprise.inject.Produces} mechanism or query the factory manually. When using the CDI
 * mechanism, the factory will create a new {@link StorageService} instance based on the storage configuration in the
 * <em>storage.properties</em> file. The created instance will be instantiated only one time (thus acting as a
 * singleton) and return the same element in each injection point. Enforcing the creation of a new instance of the
 * default configuration can be done by calling {@link #createNewDefaultStorageService()}.
 * <p>
 * To create a new non-persistent dummy storage service, call {@link #createTemporaryStorageService()}.
 */
@ApplicationScoped
public class StorageServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceFactory.class);

    private static final String STORAGE_CONFIG_FILE = "storage.properties";

    private String defaultStorageServiceName;

    private StorageEngineWrapper defaultStorageService;

    private Map<String, StorageServiceProvider> storageServiceMap = new HashMap<>();

    private Map<String, String> defaultStorageConfigMap = new HashMap<>();

    private List<StorageEngineWrapper> instantiatedStorageEngines = new ArrayList<>();

    @Inject
    private Instance<StorageServiceProvider> storageServiceProviders;

    @PostConstruct
    public void initialize() {
        LOGGER.info("Registering storage service providers ...");
        for (StorageServiceProvider storageServiceProvider : storageServiceProviders) {
            try {
                registerStorageService(storageServiceProvider);
            } catch (Exception e) {
                LOGGER.warn("Registering storage service provider from class '{}' has failed", storageServiceProvider.getClass().getCanonicalName(), e);
            }
        }
        validateStorageProviders();
        initializeDefaultConfiguration();
    }

    @PreDestroy
    public void shutdown() {
        LOGGER.info("Shutting down {} storage engine instances ...", instantiatedStorageEngines.size());

        int successfulShutdowns = 0;

        for (StorageEngineWrapper storageEngine : instantiatedStorageEngines) {
            try {
                if (!storageEngine.isShutdown())
                    storageEngine.shutdown();
                successfulShutdowns++;
            } catch (Exception e) {
                LOGGER.error("Shutting down storage engine '{}' failed", storageEngine.wrappedEngine.getClass().getCanonicalName(), e);
            }
        }

        if (successfulShutdowns != instantiatedStorageEngines.size())
            LOGGER.warn("Some storage engine instances failed to stop");
        else
            LOGGER.info("All storage engine instances have been shut down");

    }

    /**
     * Get the currently configured default storage service. The returned object acts as a singleton. Accessing the
     * default service instance should be done by simply injecting a {@link StorageService}. Calling this method
     * manually is not needed in general.
     *
     * @return the currently configured default storage service. After calling this method one time, the same instance
     * will be returned on each call.
     */
    @NotNull
    @Produces
    @DefaultStorageService
    public StorageService getDefaultStorageServiceInstance() {
        if (defaultStorageService == null)
            defaultStorageService = createNewDefaultStorageService();
        return defaultStorageService;
    }

    /**
     * Create a new temporary non-persistent storage service. Each operation on the service will be done in-memory and
     * thus be lost after shutting down Metadict. Using this service is only recommended for development and testing.
     *
     * @return a new temporary non-persistent storage service.
     */
    @NotNull
    public StorageService createTemporaryStorageService() {
        // TODO: Include this in storage lifecycle management and don't instantiate directly via new-Statement
        return new InMemoryStorage();
    }

    private void validateStorageProviders() {
        if (storageServiceMap.size() == 0) {
            LOGGER.error("No storage service provider could be found - make sure that at least one working provider is available on classpath and try again");
            throw new StorageInitializationError("No storage service provider could be found");
        }

        defaultStorageServiceName = CommonUtils.getProperty(STORAGE_CONFIG_FILE, "storage.default");

        if (!storageServiceMap.containsKey(defaultStorageServiceName)) {
            LOGGER.error("Default storage service provider '{}' could not be found", defaultStorageServiceName);
            throw new Error("Default storage service provider could not be found");
        }
    }

    private void initializeDefaultConfiguration() {
        Map<String, String> properties = CommonUtils.getProperties(STORAGE_CONFIG_FILE);

        for (String key : properties.keySet()) {
            if (StringUtils.startsWith(key, "storage." + defaultStorageServiceName + ".")) {
                String configKey = StringUtils.removeStart(key, "storage." + defaultStorageServiceName + ".");
                String configValue = properties.get(key);
                defaultStorageConfigMap.put(configKey, configValue);
                LOGGER.debug("Detected configuration key '{}' with value '{}'", configKey, configValue);
            }
        }
    }

    private void registerStorageService(@NotNull StorageServiceProvider storageServiceProvider) {
        String identifier = storageServiceProvider.getStorageBackendIdentifier();
        if (identifier == null || !CommonUtils.isValidStorageServiceName(identifier)) {
            throw new MetadictRuntimeException("Illegal storage provider identifier: " + identifier);
        }

        if (storageServiceMap.containsKey(identifier)) {
            LOGGER.error("Duplicated storage service identifier '{}' encountered - check provider classes '{}' and '{}'",
                    identifier, storageServiceProvider.getClass().getCanonicalName(), storageServiceMap.get(identifier).getClass().getCanonicalName());
        }

        storageServiceMap.put(identifier, storageServiceProvider);
        LOGGER.info("Registered storage provider service '{}'", identifier);
    }

    /**
     * Create a new instance of the currently configured default storage service. The default storage service can be
     * configured in <em>storage.properties</em>.
     *
     * @return a new instance of the currently configured default storage service.
     */
    @NotNull
    private StorageEngineWrapper createNewDefaultStorageService() {
        LOGGER.info("Creating new default storage service for '{}' ...", defaultStorageServiceName);
        StorageServiceProvider provider = storageServiceMap.get(defaultStorageServiceName);
        StorageEngineWrapper storageService = internalInstantiateStorageService(provider, defaultStorageConfigMap);
        LOGGER.info("Created new default storage service");
        return storageService;
    }

    private StorageEngineWrapper internalInstantiateStorageService(StorageServiceProvider provider, Map<String, String> storageConfigurationMap) {
        try {
            StorageEngine newStorageService = provider.createNewStorageService(storageConfigurationMap);
            StorageEngineWrapper storageEngineWrapper = new StorageEngineWrapper(newStorageService);
            this.instantiatedStorageEngines.add(storageEngineWrapper);
            LOGGER.debug("Successfully instantiated new storage service with class '{}' and configuration '{}'", newStorageService.getClass().getCanonicalName(), storageConfigurationMap);
            return storageEngineWrapper;
        } catch (Exception e) {
            LOGGER.error("Fatal error during storage service instantiation", e);
            throw e;
        }
    }

}
