package org.xlrnet.metadict.web.middleware.db;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.exception.MetadictRuntimeException;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Bundle which provides support for performing liquibase migrations on application startup.
 */
public class LiquibaseMigrationBundle implements ConfiguredBundle<DatabaseEnabledConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseMigrationBundle.class);

    /**
     * Name of the file which contains liquibase changes.
     */
    private static final String CHANGELOG_FILE = "database.xml";

    @Override
    public void run(DatabaseEnabledConfiguration configuration, Environment environment) throws Exception {
        LOGGER.info("Applying database migrations...");
        Connection connection = DriverManager.getConnection(configuration.getDatabaseConfiguration().getConnection());
        DatabaseConnection liquibaseConnection = getLiquibaseConnection(configuration, connection);
        Liquibase liquibase = new Liquibase(CHANGELOG_FILE, new ClassLoaderResourceAccessor(), liquibaseConnection);
        liquibase.update("");
        connection.commit();
        LOGGER.info("Finished database migrations");
        connection.close();
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // No initialization necessary
    }

    @NotNull
    private DatabaseConnection getLiquibaseConnection(DatabaseEnabledConfiguration configuration, Connection connection) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        Class<? extends JdbcConnection> liquibaseConnectionClass = configuration.getDatabaseConfiguration().getDbms().getLiquibaseConnectionClass();
        Constructor<? extends JdbcConnection> constructor = liquibaseConnectionClass.getConstructor(Connection.class);
        if (constructor == null) {
            throw new MetadictRuntimeException("No suitable constructor found for class " + liquibaseConnectionClass.getCanonicalName());
        }
        return constructor.newInstance(connection);
    }
}
