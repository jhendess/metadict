/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.auth.User;
import org.xlrnet.metadict.web.auth.RegistrationRequestData;
import org.xlrnet.metadict.web.auth.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;

/**
 * REST resource for registering a new user account.
 */
@Path("/register")
public class RegistrationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationResource.class);

    private final UserService userService;

    @Inject
    public RegistrationResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user account. The given {@link RegistrationRequestData} must be valid, i.e. matching bean
     * constraints or the registration will be blocked.
     *
     * @param registrationRequestData
     *         The registration request.
     * @return Either a response with {@link javax.ws.rs.core.Response.Status#ACCEPTED} if the registration was
     * successful or either 422 if any validation errors occurredor {@link javax.ws.rs.core.Response.Status#CONFLICT} if
     * the user already exists. If the registration was successful, the user will also be logged automatically.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@Valid RegistrationRequestData registrationRequestData) {
        Optional<User> newUser = userService.createNewUser(registrationRequestData.getName(), registrationRequestData.getPassword());
        Response.ResponseBuilder responseBuilder;

        if (!newUser.isPresent()) {
            responseBuilder = Response.status(Response.Status.CONFLICT);
        } else {
            Optional<User> user = userService.authenticateWithPassword(registrationRequestData.getName(),
                    registrationRequestData.getPassword());
            if (Objects.equals(newUser.get(), user.get())) {
                responseBuilder = Response.accepted();
            } else {
                // This should never happen
                responseBuilder = Response.serverError();
                LOGGER.error("User authentication after registration failed");
            }
        }
        return responseBuilder.build();
    }

}
