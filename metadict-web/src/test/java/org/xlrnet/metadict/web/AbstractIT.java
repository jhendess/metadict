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

package org.xlrnet.metadict.web;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.ClassRule;
import org.xlrnet.metadict.web.auth.entities.Credentials;
import org.xlrnet.metadict.web.auth.entities.PersistedUser;
import org.xlrnet.metadict.web.auth.entities.RegistrationRequestData;
import org.xlrnet.metadict.web.history.entities.QueryLogEntry;
import org.xlrnet.metadict.web.middleware.app.MappedJsonConfiguration;
import org.xlrnet.metadict.web.util.CleanupAccess;
import org.xlrnet.metadict.web.util.UnitOfWorkRunner;
import ru.vyarus.dropwizard.guice.injector.lookup.InjectorLookup;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Abstract class for integration tests.
 */
public class AbstractIT {

    @ClassRule
    public static final DropwizardAppRule<MappedJsonConfiguration> RULE =
            new DropwizardAppRule<>(TestApplication.class, ResourceHelpers.resourceFilePath("integration-tests.yaml"));

    private static final int INTEGRATION_TEST_PORT = 14268;

    private WebTarget target;

    private CleanupAccess cleanupAccess;

    @Before
    public void setup() {
        target = ClientBuilder.newClient().target(String.format("http://localhost:%d/api", INTEGRATION_TEST_PORT));
        cleanupAccess = getBean(CleanupAccess.class);

        runAsUnitOfWork(this::cleanupBeforeTest);
    }

    protected <T> T getBean(Class<T> clazz) {
        return InjectorLookup.getInjector(RULE.getApplication()).get().getBinding(clazz).getProvider().get();
    }

    private Void cleanupBeforeTest() {
        cleanupAccess.deleteAll(PersistedUser.class);
        cleanupAccess.deleteAll(QueryLogEntry.class);
        return null;
    }

    /**
     * Create a new test user with random name and password and login.
     * @return The session cookie for the user.
     */
    @NotNull
    protected NewCookie registerAndLogin(){
        return registerAndLogin(RandomStringUtils.randomAlphabetic(12), "qwe123");
    }

    @NotNull
    protected NewCookie registerAndLogin(String testUsername, String testPassword) {
        // Register new user:
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setName(testUsername).setPassword(testPassword).setConfirmPassword(testPassword);
        Response registrationResponse = getTarget().path("/register").request().post(Entity.json(registrationRequestData));
        assertEquals("User registration failed - this indicates an error in the authentication subsystem", Response.Status.ACCEPTED.getStatusCode(), registrationResponse.getStatus());

        // Login:
        Credentials credentials = new Credentials(testUsername, testPassword, false);
        Response loginResponse = getTarget().path("/session").request().post(Entity.json(credentials));
        assertEquals("User login failed - this indicates an error in the authentication subsystem", Response.Status.OK.getStatusCode(), loginResponse.getStatus());

        NewCookie loginCookie = loginResponse.getCookies().get("sessionToken");
        assertTrue("Session cookie may not be empty - this indicates an error in the authentication subsystem", StringUtils.isNotEmpty(loginCookie.getValue()));
        return loginCookie;
    }

    protected <T> T runAsUnitOfWork(Supplier<T> supplier) {
        return getBean(UnitOfWorkRunner.class).runAsUnitOfWork(supplier);
    }

    protected WebTarget getTarget() {
        return target;
    }
}
