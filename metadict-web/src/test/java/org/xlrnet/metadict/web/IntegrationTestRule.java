/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
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
package org.xlrnet.metadict.web;

import ch.qos.logback.classic.Level;
import io.dropwizard.Application;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.logging.DefaultLoggingFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.DropwizardTestSupport;
import org.junit.rules.ExternalResource;
import org.xlrnet.metadict.web.middleware.app.MappedJsonConfiguration;

import java.net.URI;

/**
 * Inspired by DropwizardClientRule (which sadly cannot register bundles)
 */
public class IntegrationTestRule extends ExternalResource {

    private final DropwizardTestSupport<MappedJsonConfiguration> testSupport;

    public IntegrationTestRule() {
        MappedJsonConfiguration configuration = new MappedJsonConfiguration();
        ((DefaultLoggingFactory) configuration.getLoggingFactory()).setLevel(Level.DEBUG);
        this.testSupport = new DropwizardTestSupport<MappedJsonConfiguration>(FakeApplication.class, configuration) {
            @Override
            public Application<MappedJsonConfiguration> newApplication() {
                return new FakeApplication();
            }
        };
    }

    public URI baseUri() {
        return URI.create("http://localhost:" + testSupport.getLocalPort() + "/application");
    }

    public DropwizardTestSupport<MappedJsonConfiguration> getSupport() {
        return testSupport;
    }

    @Override
    protected void before() throws Throwable {
        testSupport.before();
    }

    @Override
    protected void after() {
        testSupport.after();
    }

    private class FakeApplication extends MetadictApplication {

        @Override
        public void run(MappedJsonConfiguration configuration, Environment environment) throws Exception {
            //choose a random port
            SimpleServerFactory serverConfig = new SimpleServerFactory();
            configuration.setServerFactory(serverConfig);
            HttpConnectorFactory connectorConfig = (HttpConnectorFactory) serverConfig.getConnector();
            connectorConfig.setPort(0);

            super.run(configuration, environment);
        }

    }
}
