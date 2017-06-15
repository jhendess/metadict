/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.xlrnet.metadict.web.resources;

import io.dropwizard.auth.Auth;
import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookiePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.web.api.ResponseContainer;
import org.xlrnet.metadict.web.auth.entities.JwtPrincipal;
import org.xlrnet.metadict.web.auth.entities.Credentials;
import org.xlrnet.metadict.web.auth.services.SessionService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST resource for creating a new user session or closing the current.
 */
@Path("/session")
public class SessionResource {

    private static Logger LOGGER = LoggerFactory.getLogger(SessionResource.class);

    private final SessionService sessionService;

    @Inject
    public SessionResource(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context ContainerRequestContext requestContext, @NotNull @Valid Credentials credentials) {
        JwtCookiePrincipal principal = sessionService.startSession(credentials, requestContext);

        try {
            // Sleep for a while to avoid brute-force attacks
            Thread.sleep(500);
        } catch (InterruptedException e) {
            LOGGER.debug("Unexpected interruption while sleeping", e);
        }

        if (principal == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            return Response.ok(ResponseContainer.fromSuccessful(principal)).build();
        }
    }

    @DELETE
    public Response logout(@Context ContainerRequestContext requestContext) {
        sessionService.stopSession(requestContext);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response testAuthenticatedResource(@Auth JwtPrincipal principal) {
        return Response.ok(ResponseContainer.fromSuccessful(principal)).build();
    }
}
