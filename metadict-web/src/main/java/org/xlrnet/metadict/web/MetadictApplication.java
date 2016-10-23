/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakob Hendeß
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

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.engines.heinzelnisse.HeinzelnisseEngineProvider;
import org.xlrnet.metadict.engines.leo.LeoEngineProvider;
import org.xlrnet.metadict.engines.nobordbok.OrdbokEngineProvider;
import org.xlrnet.metadict.engines.woxikon.WoxikonEngineProvider;
import org.xlrnet.metadict.web.app.MetadictConfiguration;
import org.xlrnet.metadict.web.app.WebModule;
import org.xlrnet.metadict.web.middleware.injection.GovernatorInjectorFactory;
import org.xlrnet.metadict.web.middleware.jackson.JacksonUtils;
import ru.vyarus.dropwizard.guice.GuiceBundle;

/**
 * Standalone bootstrap application using dropwizard.
 */
public class MetadictApplication extends Application<MetadictConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadictApplication.class);

    public static void main(String[] args) throws Exception {
        new MetadictApplication().run(args);
    }

    @Override
    public void run(MetadictConfiguration metadictConfiguration, Environment environment) throws Exception {
    }

    @Override
    public void initialize(Bootstrap<MetadictConfiguration> bootstrap) {
        bootstrap.addBundle(
                GuiceBundle.<MetadictConfiguration>builder()
                        .injectorFactory(new GovernatorInjectorFactory())
                        .modules(new WoxikonEngineProvider(),
                                new HeinzelnisseEngineProvider(),
                                new LeoEngineProvider(),
                                new OrdbokEngineProvider(),
                                new WebModule())
                        .enableAutoConfig(getClass().getPackage().getName())
                        .build()
        );

        JacksonUtils.configureObjectMapper(bootstrap.getObjectMapper());
    }
}
