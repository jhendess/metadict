package org.xlrnet.metadict.web.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

public class AbstractAccess<E> extends AbstractDAO<E> {

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory
     *         a session provider
     */
    public AbstractAccess(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E persist(E entity) {
        return super.persist(entity);
    }
}
