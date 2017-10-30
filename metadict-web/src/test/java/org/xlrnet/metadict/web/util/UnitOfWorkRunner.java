package org.xlrnet.metadict.web.util;

import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import java.util.function.Supplier;

/**
 * Utility class which runs code programmatically in a dedicated unit of work.
 */
public class UnitOfWorkRunner {

    @Inject
    public UnitOfWorkRunner(SessionFactory sessionFactory) {
        // It seems that there must be at least one injected object to make this proxy runner work
    }

    @UnitOfWork
    public <T> T runAsUnitOfWork(Supplier<T> supplier) {
        return supplier.get();
    }
}
