package org.xlrnet.metadict.web.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.web.auth.entities.Credentials;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

/**
 * Health check which tests whether the session login / logout works correctly by perform a REST call to the local
 * session resource.
 */
public class SessionHealthCheck extends NamedHealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHealthCheck.class);

    private final SessionHealthCheckHelper sessionHealthCheckHelper;

    @Inject
    public SessionHealthCheck(SessionHealthCheckHelper sessionHealthCheckHelper) {
        this.sessionHealthCheckHelper = sessionHealthCheckHelper;
    }

    @Override
    public String getName() {
        return "session";
    }


    @Override
    protected Result check() throws Exception {
        LOGGER.info("Begin session health check");

        // Login:
        Credentials credentials = new Credentials(sessionHealthCheckHelper.getTechnicalUserName(), sessionHealthCheckHelper.getTechnicalUserPassword(), false);
        Response loginResponse = sessionHealthCheckHelper.getSessionResource().request().post(Entity.json(credentials));
        if (Response.Status.OK.getStatusCode() != loginResponse.getStatus()) {
            LOGGER.warn("Login request failed: {}", loginResponse.getStatus());
            return Result.unhealthy("Login request failed", loginResponse);
        }

        NewCookie loginCookie = loginResponse.getCookies().get("sessionToken");

        // Check if protected resource is accessible
        Response sessionLoggedIn = sessionHealthCheckHelper.getSessionResource().request().cookie(loginCookie).get();
        if (Response.Status.OK.getStatusCode() != sessionLoggedIn.getStatus()) {
            LOGGER.warn("User wasn't logged in after successful login response: {}", sessionLoggedIn.getStatus());
            return Result.unhealthy("User wasn't logged in after successful login response", sessionLoggedIn);
        }

        LOGGER.info("Session health check finished successfully");
        return Result.healthy();
    }
}
