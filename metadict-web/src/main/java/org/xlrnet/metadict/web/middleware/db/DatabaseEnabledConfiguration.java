package org.xlrnet.metadict.web.middleware.db;

/**
 * Configuration which supports a database backend.
 */
public interface DatabaseEnabledConfiguration {

    DatabaseConfiguration getDatabaseConfiguration();

    /**
     * Configuration class for a database management system which is used as the backend.
     */
    interface DatabaseConfiguration {

        public String getConnection();

        public DatabaseType getDbms();
    }
}
