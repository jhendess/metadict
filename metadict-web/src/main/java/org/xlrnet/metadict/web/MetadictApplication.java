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
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookieAuthBundle;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import org.xlrnet.metadict.core.CoreModule;
import org.xlrnet.metadict.engines.SearchEnginesModule;
import org.xlrnet.metadict.web.middleware.app.MappedJsonConfiguration;
import org.xlrnet.metadict.web.middleware.app.MetadictServletModule;
import org.xlrnet.metadict.web.middleware.app.WebModule;
import org.xlrnet.metadict.web.middleware.bundles.SinglePageAppAssetsBundle;
import org.xlrnet.metadict.web.middleware.injection.GovernatorInjectorFactory;
import org.xlrnet.metadict.web.middleware.jackson.JacksonUtils;
import ru.vyarus.dropwizard.guice.GuiceBundle;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.Locale;

/**
 * Standalone bootstrap application using dropwizard.
 */
public class MetadictApplication extends Application<MappedJsonConfiguration> {

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);  // Reset the locale VM-wide
        new MetadictApplication().run(args);
    }

    @Override
    public void run(MappedJsonConfiguration metadictConfiguration, Environment environment) throws Exception {
        // Define default mapping URL pattern for resources
        environment.jersey().setUrlPattern("/api/*");
        // Configure url rewrite filter
        FilterRegistration.Dynamic rewrite = environment.servlets()
                .addFilter("UrlRewriteFilter", new UrlRewriteFilter());
        rewrite.setInitParameter("confPath", "urlrewrite.xml");
        rewrite.addMappingForUrlPatterns(EnumSet.of(DispatcherType.FORWARD, DispatcherType.REQUEST), true, "/*");
    }

    @Override
    public void initialize(Bootstrap<MappedJsonConfiguration> bootstrap) {
        // Start Guicey container
        bootstrap.addBundle(
                GuiceBundle.<MappedJsonConfiguration>builder()
                        .injectorFactory(new GovernatorInjectorFactory())
                        .bindConfigurationInterfaces()
                        .modules(
                                new CoreModule(),
                                new SearchEnginesModule(),
                                new MetadictServletModule(),
                                new WebModule()
                        )
                        .enableAutoConfig(getClass().getPackage().getName())
                        .build()
        );

        // Enable JWT cookie authentication
        bootstrap.addBundle(JwtCookieAuthBundle.getDefault().withConfigurationSupplier(
                (Configuration c) -> ((MappedJsonConfiguration) c).getJwtCookieAuth())
        );

        // Serve static content (i.e. app)
        bootstrap.addBundle(new SinglePageAppAssetsBundle("/static", "/"));

        // Install custom Jackson mapping
        JacksonUtils.configureObjectMapper(bootstrap.getObjectMapper());
    }
}
