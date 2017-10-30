package org.xlrnet.metadict.web;

import io.dropwizard.setup.Bootstrap;
import org.xlrnet.metadict.web.middleware.app.MappedJsonConfiguration;
import org.xlrnet.metadict.web.middleware.injection.GovernatorInjectorFactory;

public class TestApplication extends MetadictApplication {

    @Override
    public void initialize(Bootstrap<MappedJsonConfiguration> bootstrap) {
        GovernatorInjectorFactory.override(new TestModule());
        super.initialize(bootstrap);
    }
}
