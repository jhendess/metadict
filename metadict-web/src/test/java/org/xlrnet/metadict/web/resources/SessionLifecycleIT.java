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

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.commons.lang3.StringUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.web.MetadictApplication;
import org.xlrnet.metadict.web.api.Credentials;
import org.xlrnet.metadict.web.app.MappedJsonConfiguration;
import org.xlrnet.metadict.web.auth.RegistrationRequestData;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

/**
 * Test the complete user session lifecycle (registration, login, logout).
 */
public class SessionLifecycleIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionLifecycleIT.class);

    @ClassRule
    public static final DropwizardAppRule<MappedJsonConfiguration> RULE =
            new DropwizardAppRule<>(MetadictApplication.class, ResourceHelpers.resourceFilePath("integration-tests.yaml"));

    private final WebTarget target = ClientBuilder.newClient().target(String.format("http://localhost:%d/api", RULE.getLocalPort()));

    private static final String TEST_PASSWORD = "TEST_PASSWORD";

    private static final String TEST_USERNAME = "TEST_USERNAME";

    @Test
    public void testSessionLifecycle() {
        // Verify that no user is logged in:
        Response sessionBeforeGet = target.path("/session").request().get();
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), sessionBeforeGet.getStatus());

        // Register new user:
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setName(TEST_USERNAME).setPassword(TEST_PASSWORD).setPasswordCopy(TEST_PASSWORD);
        Response registrationResponse = target.path("/register").request().post(Entity.json(registrationRequestData));
        assertEquals("User registration failed", Response.Status.ACCEPTED.getStatusCode(), registrationResponse.getStatus());

        // Login:
        Credentials credentials = new Credentials(TEST_USERNAME, TEST_PASSWORD);
        Response loginResponse = target.path("/session").request().post(Entity.json(credentials));
        assertEquals("User login failed", Response.Status.OK.getStatusCode(), loginResponse.getStatus());

        NewCookie loginCookie = loginResponse.getCookies().get("sessionToken");
        assertTrue("Session cookie may not be empty", StringUtils.isNotEmpty(loginCookie.getValue()));

        // Check if protected resource is accessible
        Response sessionLoggedIn = target.path("/session").request().cookie(loginCookie).get();
        assertEquals(Response.Status.OK.getStatusCode(), sessionLoggedIn.getStatus());

        // Logout:
        Response deleteResponse = target.path("/session").request().cookie(loginCookie).delete();
        Cookie logoutCookie = deleteResponse.getCookies().get("sessionToken");
        assertNotNull(logoutCookie);
        assertEquals("", logoutCookie.getValue());

        // Verify that no user is logged in:
        Response sessionAfterLogout = target.path("/session").request().get();
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), sessionAfterLogout.getStatus());
    }

}