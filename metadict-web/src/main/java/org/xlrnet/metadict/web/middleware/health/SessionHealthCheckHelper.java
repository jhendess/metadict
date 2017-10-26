package org.xlrnet.metadict.web.middleware.health;

import io.dropwizard.setup.Environment;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.storage.StorageBackendException;
import org.xlrnet.metadict.web.auth.services.UserService;
import org.xlrnet.metadict.web.middleware.util.CryptoUtils;
import org.xlrnet.metadict.web.util.ConversionUtils;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Helper class for the session health check. Creates a random new technical user on startup and removes it on
 * application shutdown.
 */
@Singleton
public class SessionHealthCheckHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHealthCheckHelper.class);

    private static final String SESSION_RESOURCE_PATH = "session";

    /**
     * Configuration of the server environment.
     */
    private final Environment environment;

    /**
     * User service used for creating new technical user on startup.
     */
    private final UserService userService;

    /**
     * Name of the technical user that will be used for checking.
     */
    private String technicalUserName;

    /**
     * Password for the technical user that will be used for checking.
     */
    private String technicalUserPassword;

    private WebTarget sessionResource;

    @Inject
    public SessionHealthCheckHelper(Environment environment, UserService userService) {
        this.environment = environment;
        this.userService = userService;

        // Prepare the technical user once the server has finished booting
        environment.lifecycle().addServerLifecycleListener(server -> prepareTechnicalUser());
    }

    private void prepareTechnicalUser() {
        this.technicalUserPassword = ConversionUtils.byteArrayToHexString(CryptoUtils.generateRandom(16));
        this.technicalUserName = this.userService.createTechnicalUser(this.technicalUserPassword).getName();

        Server server = this.environment.getApplicationContext().getServer();
        String contextPath = StringUtils.removeStart(this.environment.getApplicationContext().getContextPath(), "/");
        String basePath = server.getURI().toString() + contextPath + "api/";
        this.sessionResource = ClientBuilder.newClient().target(basePath).path(SESSION_RESOURCE_PATH);
        LOGGER.debug("Initialized session health check for {} using user {}", this.sessionResource.getUri().toString(), this.technicalUserName);
    }

    @PreDestroy
    public void stop() throws StorageBackendException {
        this.userService.removeUser(this.technicalUserName);
        LOGGER.debug("Removed technical user {} for session health check", this.technicalUserName);
    }

    public String getTechnicalUserName() {
        return this.technicalUserName;
    }

    public String getTechnicalUserPassword() {
        return this.technicalUserPassword;
    }

    public WebTarget getSessionResource() {
        return this.sessionResource;
    }
}
