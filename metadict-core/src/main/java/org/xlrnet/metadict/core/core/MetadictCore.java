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

package org.xlrnet.metadict.core.core;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.core.autotest.AutoTestReport;
import org.xlrnet.metadict.core.query.QueryManager;
import org.xlrnet.metadict.core.query.QueryRequest;
import org.xlrnet.metadict.core.query.QueryRequestBuilder;
import org.xlrnet.metadict.core.query.QueryResponse;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * The main entry point for accessing and querying Metadict. To access this component, you should inject it through
 * {@link Inject} inside a CDI container (e.g. JBoss Weld).
 * <p>
 * Since this object is {@link javax.enterprise.context.ApplicationScoped}, only one instance will be running at the
 * same time.
 */
@ApplicationScoped
public class MetadictCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadictCore.class);

    @Inject
    private EngineRegistry engineRegistry;

    @Inject
    private QueryManager queryManager;

    /**
     * Creates a new builder for creating {@link QueryRequest} objects. Use this method to prepare your queries.
     *
     * @return a new builder for creating query requests.
     */
    @NotNull
    public QueryRequestBuilder createNewQueryRequestBuilder() {
        return queryManager.createNewQueryRequestBuilder();
    }

    /**
     * Execute all auto test cases for all registered engines and return a {@link AutoTestReport}.
     *
     * @return an {@link AutoTestReport} for all executed test cases.
     */
    @NotNull
    public AutoTestReport executeAllAutoTests() {
        return engineRegistry.getAutoTestManager().runAllRegisteredAutoTests();
    }

    @NotNull
    public QueryResponse executeRequest(QueryRequest queryRequest) {
        return queryManager.executeQuery(queryRequest);
    }

    /**
     * Return a reference to the internal {@link EngineRegistry} of this instance. You can use it for viewing the
     * currently registered engines and their implemented {@link org.xlrnet.metadict.api.metadata.FeatureSet} and
     * {@link
     * org.xlrnet.metadict.api.metadata.EngineDescription}.
     * <p>
     * However, you should <i>never</i> query the engines directly.
     *
     * @return a reference to the internal engine registry.
     */
    public EngineRegistry getEngineRegistry() {
        return engineRegistry;
    }

    /**
     * Returns the current system status.
     *
     * @return the current system status.
     */
    public SystemStatus getSystemStatus() {
        return SystemStatus.queryStatus();
    }

    @PostConstruct
    private void initialize() {
        SystemStatus.initialize();
        LOGGER.info("Metadict Core booted with {} search engines.", engineRegistry.countRegisteredEngines());
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down Metadict Core...");
    }
}
