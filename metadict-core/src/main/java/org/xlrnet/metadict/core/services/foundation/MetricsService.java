package org.xlrnet.metadict.core.services.foundation;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central singleton service which exposes {@link MetricRegistry} objects. Inject this class to measure internal
 * performance metrics.
 */
@Singleton
public class MetricsService {

    private final ConcurrentHashMap<String, MetricRegistry> metricRegistryMap = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsService.class);

    /**
     * Returns a thread-safe {@link MetricRegistry} for the given name. If the registry doesn't exist, it will be
     * created. If metrics are disabled, a dummy object will be returned.
     *
     * @param name
     *         the first element of the name
     * @param names
     *         the remaining elements of the name
     * @return a {@link MetricRegistry} for the given name.
     */
    public MetricRegistry getRegistryByName(String name, String... names) {
        String registryName = MetricRegistry.name(name, names);
        return metricRegistryMap.computeIfAbsent(registryName, n -> {
            MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(name);
            return initializeNewReporter(metricRegistry);
        });
    }

    /**
     * Returns a thread-safe {@link MetricRegistry} for the given name. If the registry doesn't exist, it will be
     * created. If metrics are disabled, a dummy object will be returned.
     *
     * @param clazz
     *         the first element of the name
     * @param names
     *         the remaining elements of the name
     * @return a {@link MetricRegistry} for the given name.
     */
    public MetricRegistry getRegistryByName(Class<?> clazz, String... names) {
        return getRegistryByName(clazz.getName(), names);
    }

    /**
     * Returns a collection of all available {@link MetricRegistry} objects. Contains only registries which have been
     * opened at least one time.
     *
     * @return a collection of all available metric registries.
     */
    public Collection<MetricRegistry> listRegistryNames() {
        Set<String> names = SharedMetricRegistries.names();
        Set<MetricRegistry> registries = new HashSet<>(names.size());
        for (String s : names) {
            registries.add(SharedMetricRegistries.getOrCreate(s));
        }
        return registries;
    }

    private MetricRegistry initializeNewReporter(MetricRegistry metricRegistry) {
        JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).build();
        reporter.start();
        return metricRegistry;
    }
}
