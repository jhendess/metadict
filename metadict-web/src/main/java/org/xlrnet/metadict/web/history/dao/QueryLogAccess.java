package org.xlrnet.metadict.web.history.dao;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.xlrnet.metadict.web.auth.entities.PersistedUser;
import org.xlrnet.metadict.web.db.dao.AbstractAccess;
import org.xlrnet.metadict.web.history.entities.QueryLogEntry;

import javax.inject.Inject;
import java.util.List;

public class QueryLogAccess extends AbstractAccess<QueryLogEntry> {

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory
     *         a session provider
     */
    @Inject
    public QueryLogAccess(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Returns query log entries for a given persisted user sorted descendingly by request time (i.e. newest first).
     * @param user The user for which the entries should be returned.
     * @param offset The query log offset.
     * @param size Number of entries which should be fetched.
     * @return
     */
    public List<QueryLogEntry> findQueryLogEntriesPaged(PersistedUser user, int offset, int size) {
        List<QueryLogEntry> resultList = query("SELECT E FROM QueryLogEntry E WHERE E.user = :user ORDER BY E.requestTime DESC")
                .setParameter("user", user)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
        for (QueryLogEntry queryLogEntry : resultList) {
            Hibernate.initialize(queryLogEntry.getMonolingualLanguages());
        }

        return resultList;
    }
}
