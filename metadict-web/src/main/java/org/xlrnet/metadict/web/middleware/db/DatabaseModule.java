package org.xlrnet.metadict.web.middleware.db;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;

import javax.ws.rs.Path;

/**
 * Guice module to configure Hibernate's {@link SessionFactory} and install a custom transactional interceptor.
 */
public class DatabaseModule extends AbstractModule {

    private final DatabaseBundle bundle;

    public DatabaseModule(DatabaseBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    protected void configure() {
        bind(SessionFactory.class).toInstance(bundle.getSessionFactory());
        // Register a custom interceptor which supports @UnitOfWork outside of resources.
        bindInterceptor(Matchers.any(),
                Matchers.annotatedWith(UnitOfWork.class)
                        .and(Matchers.not(Matchers.annotatedWith(Path.class))),
                new UnitOfWorkInterceptor(getProvider(SessionFactory.class)));
    }
}