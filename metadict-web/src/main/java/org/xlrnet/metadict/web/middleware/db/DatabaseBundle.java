package org.xlrnet.metadict.web.middleware.db;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import org.xlrnet.metadict.web.middleware.app.MappedJsonConfiguration;

/**
 * Bundle which provides initial database configuration. This class overrides most available configuration of the
 * default dropwizard database configuration API with custom values.
 */
public class DatabaseBundle extends ScanningHibernateBundle<MappedJsonConfiguration> {

    /**
     * Package name to scan for entities.
     */
    private static final String PACKAGE_NAME = "org.xlrnet.metadict";

    /**
     * Hardcoded JDBC driver class of the database to use.
     */
    private static final String HSQLDB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    /**
     * Hardcoded query to check if the database is still alive.
     */
    private static final String VALIDATION_QUERY = "/* Metadict Health Check */ SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";

    public DatabaseBundle() {
        super(PACKAGE_NAME);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(MappedJsonConfiguration configuration) {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.setDriverClass(HSQLDB_DRIVER);
        dataSourceFactory.setValidationQuery(VALIDATION_QUERY);
        dataSourceFactory.setUrl("jdbc:hsqldb:mem:metadict");    // TODO: Make this configurable by the user (at least the filename)
        dataSourceFactory.setCheckConnectionWhileIdle(false);
        return dataSourceFactory;
    }
}