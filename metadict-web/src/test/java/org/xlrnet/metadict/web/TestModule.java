package org.xlrnet.metadict.web;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.xlrnet.metadict.api.engine.SearchEngineProvider;
import org.xlrnet.metadict.web.middleware.services.DummyRateControlService;
import org.xlrnet.metadict.web.middleware.services.RateControlService;

/**
 * Module which overrides test classes.
 */
public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        // Disable rate control in tests
        bind(RateControlService.class).to(DummyRateControlService.class);
        // Disable default search engines
        Multibinder<SearchEngineProvider> binder = Multibinder.newSetBinder(binder(), SearchEngineProvider.class);
    }
}
