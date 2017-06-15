package org.xlrnet.metadict.web.auth.services;

import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookiePrincipal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.auth.Role;
import org.xlrnet.metadict.api.auth.User;
import org.xlrnet.metadict.web.auth.entities.JwtPrincipal;
import org.xlrnet.metadict.web.auth.entities.Credentials;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Service which contains the logic for managing sessions.
 */
public class SessionService {

    private static Logger LOGGER = LoggerFactory.getLogger(SessionService.class);

    private final UserService userService;

    @Inject
    public SessionService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Starts a new session using the given credentials and sets the principal in the request context accordingly if the
     * session could be started successfully (i.e. credentials were correct).
     *
     * @param credentials
     *         The credentials used for logging in.
     * @param requestContext
     *         The request context in which the session information should be set.
     * @return The {@link JwtCookiePrincipal} for the principal or null, if no session could be started.
     */
    @Nullable
    public JwtCookiePrincipal startSession(@NotNull Credentials credentials, @NotNull ContainerRequestContext requestContext) {
        JwtPrincipal principal = null;
        Optional<User> user = this.userService.authenticateWithPassword(credentials.getName(), credentials.getPassword());

        if (user.isPresent()) {
            Collection<String> roles = new ArrayList<>();

            for (Role role : user.get().getRoles()) {
                roles.add(role.getId());
            }

            principal = new JwtPrincipal(credentials.getName(), false, roles, null);
            if (credentials.isStayLoggedIn()) {
                principal.setPresistent(true);
            }
            principal.addInContext(requestContext);
            LOGGER.info("User {} started a new session", principal.getName());
        }

        return principal;
    }

    /**
     * Stops the currently active session.
     *
     * @param requestContext
     *         The request in which the session should be stopped.
     */
    public void stopSession(ContainerRequestContext requestContext) {
        JwtCookiePrincipal.removeFromContext(requestContext);
    }
}
