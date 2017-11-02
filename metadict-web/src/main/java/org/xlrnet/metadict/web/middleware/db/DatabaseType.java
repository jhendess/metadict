package org.xlrnet.metadict.web.middleware.db;

import liquibase.database.jvm.HsqlConnection;
import liquibase.database.jvm.JdbcConnection;

/**
 * Enumeration of supported databases.
 */
public enum DatabaseType {
    /** HyperSQL database. */
    HSQLDB("org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:", "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS", HsqlConnection.class);

    /** The prefix which must be matched by the connection URL. */
    private final String connectionPrefix;

    /** The JDBC driver which will be used. */
    private final String jdbcDriver;

    /** The validation query for the database health check. */
    private final String validationQuery;

    /** The class to use by liquibase data migrations. */
    private final Class<? extends JdbcConnection> liquibaseConnectionClass;

    DatabaseType(String jdbcDriver, String connectionPrefix, String validationQuery, Class<? extends JdbcConnection> liquibaseConnectionClass) {
        this.jdbcDriver = jdbcDriver;
        this.connectionPrefix = connectionPrefix;
        this.validationQuery = validationQuery;
        this.liquibaseConnectionClass = liquibaseConnectionClass;
    }

    public String getConnectionPrefix() {
        return connectionPrefix;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public Class<? extends JdbcConnection> getLiquibaseConnectionClass() {
        return liquibaseConnectionClass;
    }
}
