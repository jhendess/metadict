package org.xlrnet.metadict.web.auth.dao;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.web.auth.entities.PersistedUser;
import org.xlrnet.metadict.web.db.dao.AbstractAccess;

import javax.inject.Inject;
import java.util.Optional;

public class UserAccess extends AbstractAccess<PersistedUser> {

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory
     *         a session provider
     */
    @Inject
    public UserAccess(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Tries to load a user with the given name from the database and returns it in an {@link Optional}.
     *
     * @param name
     *         The name of the user to find.
     * @return An Optional which contains either the user if it exists or is empty.
     */
    @NotNull
    public Optional<PersistedUser> findByName(@NotNull String name) {
        return Optional.ofNullable(query("SELECT U FROM PersistedUser U WHERE U.name = :name").setParameter("name", name).uniqueResult());
    }

    /**
     * Removes a user with the given name from the database.
     *
     * @param name
     *         The name of the user to delete.
     * @return True if the user was deleted or false if not.
     */
    public boolean deleteByName(@NotNull String name) {
        return currentSession().createQuery("DELETE FROM PersistedUser U WHERE U.name = :name").setParameter("name", name).executeUpdate() > 0;
    }
}
