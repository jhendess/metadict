package org.xlrnet.metadict.web.health;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.web.auth.services.UserService;
import org.xlrnet.metadict.web.middleware.util.CryptoUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.DatatypeConverter;

/**
 * Helper class for the session health check. Creates a random new technical user on startup and removes it on
 * application shutdown.
 */
@Singleton
public class SessionHealthCheckHelper implements Managed {

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

    /**
     * Base path of the application used for resource tests.
     */
    private String basePath;

    private WebTarget sessionResource;

    @Inject
    public SessionHealthCheckHelper(Environment environment, UserService userService) {
        this.environment = environment;
        this.userService = userService;
    }


    @Override
    public void start() throws Exception {
        this.technicalUserPassword = DatatypeConverter.printHexBinary(CryptoUtils.generateRandom(16));
        technicalUserName = this.userService.createTechnicalUser(technicalUserPassword).getName();

        Server server = environment.getApplicationContext().getServer();
        String contextPath = StringUtils.removeStart(environment.getApplicationContext().getContextPath(), "/");
        basePath = server.getURI().toString() + contextPath + "api/";
        sessionResource = ClientBuilder.newClient().target(basePath).path(SESSION_RESOURCE_PATH);
        LOGGER.debug("Initialized session health check for {} using user {}", sessionResource.getUri().toString(), technicalUserName);
    }

    @Override
    public void stop() throws Exception {
        this.userService.removeUser(technicalUserName);
    }

    public String getTechnicalUserName() {
        return technicalUserName;
    }

    public String getTechnicalUserPassword() {
        return technicalUserPassword;
    }

    public WebTarget getSessionResource() {
        return sessionResource;
    }
}
