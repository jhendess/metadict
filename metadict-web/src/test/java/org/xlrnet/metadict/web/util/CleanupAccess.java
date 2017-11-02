package org.xlrnet.metadict.web.util;

import io.dropwizard.hibernate.AbstractDAO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.xlrnet.metadict.web.db.entities.AbstractMetadictEntity;

import javax.inject.Inject;
import javax.persistence.Entity;

public class CleanupAccess extends AbstractDAO<Void> {

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory
     *         a session provider
     */
    @Inject
    public CleanupAccess(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Removes all instances of the given entity class.
     * @param clazz
     */
    public void deleteAll(Class<? extends AbstractMetadictEntity> clazz) {
        Entity annotation = clazz.getAnnotation(Entity.class);
        if (annotation == null) {
            throw new RuntimeException("Given class is not annotated with @Entity");
        }
        currentSession().createQuery("Delete From " + StringUtils.defaultIfBlank(annotation.name(), clazz.getSimpleName()));
    }
}
