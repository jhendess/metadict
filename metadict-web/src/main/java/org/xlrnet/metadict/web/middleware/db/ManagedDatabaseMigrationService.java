package org.xlrnet.metadict.web.middleware.db;

import io.dropwizard.lifecycle.Managed;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.web.middleware.app.MappedJsonConfiguration;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Managed service which automatically applies any outstanding database migrations on startup.
 */
public class ManagedDatabaseMigrationService implements Managed {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedDatabaseMigrationService.class);

    /**
     * Name of the file which contains liquibase changes.
     */
    private static final String CHANGELOG_FILE = "database.xml";

    /**
     * Database bundle which contains main database configuration.
     */
    private final DatabaseBundle databaseBundle;

    /**
     * Application configuration.
     */
    private MappedJsonConfiguration configuration;

    @Inject
    public ManagedDatabaseMigrationService(DatabaseBundle databaseBundle, MappedJsonConfiguration configuration) {
        this.databaseBundle = databaseBundle;
        this.configuration = configuration;
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Applying database migrations...");
        Connection connection = DriverManager.getConnection(configuration.getDatabaseConfiguration().getConnection());
        DatabaseConnection liquibaseConnection = getLiquibaseConnection(connection);
        Liquibase liquibase = new Liquibase(CHANGELOG_FILE, new ClassLoaderResourceAccessor(), liquibaseConnection);
        liquibase.update("");
        connection.commit();
        LOGGER.info("Finished database migrations");
        connection.close();
    }

    @NotNull
    private DatabaseConnection getLiquibaseConnection(Connection connection) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        Class<? extends JdbcConnection> liquibaseConnectionClass = configuration.getDatabaseConfiguration().getDbms().getLiquibaseConnectionClass();
        Constructor<? extends JdbcConnection> constructor = liquibaseConnectionClass.getConstructor(Connection.class);
        if (constructor == null) {
            throw new RuntimeException("No suitable constructor found for class " + liquibaseConnectionClass.getCanonicalName());
        }
        return constructor.newInstance(connection);
    }

    @Override
    public void stop() throws Exception {
        // No stopping necessary
    }
}
