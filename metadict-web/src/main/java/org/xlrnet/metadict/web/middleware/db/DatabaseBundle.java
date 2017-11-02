package org.xlrnet.metadict.web.middleware.db;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.exception.MetadictRuntimeException;
import org.xlrnet.metadict.web.middleware.app.MappedJsonConfiguration;

/**
 * Bundle which provides initial database configuration. This class overrides most available configuration of the
 * default dropwizard database configuration API with custom values.
 */
public class DatabaseBundle extends ScanningHibernateBundle<MappedJsonConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseBundle.class);

    /**
     * Package name to scan for entities.
     */
    private static final String PACKAGE_NAME = "org.xlrnet.metadict";

    private static final String VALIDATION_QUERY_COMMENT = "/* Metadict Health Check */ ";

    public DatabaseBundle() {
        super(PACKAGE_NAME);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(MappedJsonConfiguration configuration) {
        DataSourceFactory dataSourceFactory = configureDataSourceFactory(configuration);
        dataSourceFactory.setCheckConnectionWhileIdle(false);
        return dataSourceFactory;
    }

    private DataSourceFactory configureDataSourceFactory(MappedJsonConfiguration configuration) {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        DatabaseType databaseType = configuration.getDatabaseConfiguration().getDbms();
        String connectionString = configuration.getDatabaseConfiguration().getConnection();

        if (!StringUtils.startsWithIgnoreCase(connectionString, databaseType.getConnectionPrefix())) {
            String msg = String.format("Invalid database connection URL: \"%s\" - must start with \"%s\"", connectionString, databaseType.getConnectionPrefix());
            LOGGER.error(msg);
            throw new MetadictRuntimeException(msg);
        }

        dataSourceFactory.setDriverClass(databaseType.getJdbcDriver());
        dataSourceFactory.setUrl(configuration.getDatabaseConfiguration().getConnection());
        dataSourceFactory.setValidationQuery(VALIDATION_QUERY_COMMENT + databaseType.getValidationQuery());

        LOGGER.info("Configured new DataSourceFactory of type {} to {}", databaseType, connectionString);

        return dataSourceFactory;
    }
}