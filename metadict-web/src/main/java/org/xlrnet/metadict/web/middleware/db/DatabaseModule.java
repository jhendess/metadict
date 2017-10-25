package org.xlrnet.metadict.web.middleware.db;

import com.google.inject.AbstractModule;
import org.hibernate.SessionFactory;

/**
 * Guice module to configure Hibernate's {@link SessionFactory}.
 */
public class DatabaseModule extends AbstractModule {

    private final DatabaseBundle bundle;

    public DatabaseModule(DatabaseBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    protected void configure() {
        bind(SessionFactory.class).toInstance(bundle.getSessionFactory());
    }
}