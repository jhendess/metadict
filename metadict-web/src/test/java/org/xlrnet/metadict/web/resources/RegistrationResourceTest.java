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

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.xlrnet.metadict.core.services.storage.InMemoryStorage;
import org.xlrnet.metadict.web.auth.RegistrationRequestData;
import org.xlrnet.metadict.web.auth.UserFactory;
import org.xlrnet.metadict.web.auth.UserService;
import org.xlrnet.metadict.web.services.SequenceService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Various tests for the registration resource to test various aspects of user registration.
 */
public class RegistrationResourceTest {

    private static final InMemoryStorage storageService = new InMemoryStorage();

    private static final SequenceService sequenceService = new SequenceService();

    private static final UserFactory userFactory = new UserFactory(sequenceService);

    private static final UserService userService = spy(new UserService(storageService, userFactory));

    private static final int UNPROCESSABLE_ENTITY = 422;

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new RegistrationResource(userService))
            .build();

    private static final String TEST_PASSWORD = "TEST_PASSWORD";

    private static final String TEST_USERNAME = "TEST_USERNAME";

    private static final String TOO_SHORT_VALUE = "abcde";

    private static final String TOO_LONG_VALUE = "abcdefghiabcdefghiabcdefghiabcdefghi1";

    private static final String ILLEGAL_USERNAME = "abcdef+?.-,";

    @After
    public void tearDown() throws Exception {
        storageService.reset();
        // Reset the invocation count
        reset(userService);
    }

    @Test
    public void testEmptyRequest() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData();
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testOnlyName() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setName(TEST_PASSWORD);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testOnlyPassword() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setPassword(TEST_PASSWORD);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testOnlyPasswordDuplicate() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setConfirmPassword(TEST_PASSWORD);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testMissingUsername() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setPassword(TEST_PASSWORD).setConfirmPassword(TEST_PASSWORD);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testMissingPasswordDuplicate() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setPassword(TEST_PASSWORD).setName(TEST_USERNAME);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testWrongDuplicate() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setPassword(TEST_PASSWORD).setName(TEST_PASSWORD).setConfirmPassword(TEST_USERNAME);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testPasswordTooShort() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setName(TEST_USERNAME).setPassword(TOO_SHORT_VALUE).setConfirmPassword(TOO_SHORT_VALUE);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testPasswordTooLong() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setName(TEST_USERNAME).setPassword(TOO_LONG_VALUE).setConfirmPassword(TOO_LONG_VALUE);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testUsernameIllegalCharacter() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setName(ILLEGAL_USERNAME).setPassword(TEST_PASSWORD).setConfirmPassword(TEST_PASSWORD);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testUsernameTooShort() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setName(TOO_SHORT_VALUE).setPassword(TEST_PASSWORD).setConfirmPassword(TEST_PASSWORD);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }

    @Test
    public void testUsernameTooLong() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setName(TOO_SHORT_VALUE).setPassword(TOO_LONG_VALUE).setConfirmPassword(TOO_LONG_VALUE);
        Response post = performRequest(registrationRequestData);

        assertFailedRegistration(post);
    }


    @Test
    public void testSuccessful() {
        RegistrationRequestData registrationRequestData = new RegistrationRequestData().setName(TEST_USERNAME).setPassword(TEST_PASSWORD).setConfirmPassword(TEST_PASSWORD);
        Response post = performRequest(registrationRequestData);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), post.getStatus());
        verify(userService).createNewUser(TEST_USERNAME, TEST_PASSWORD);
    }

    private void assertFailedRegistration(Response post) {
        assertEquals(UNPROCESSABLE_ENTITY, post.getStatus());
        verifyZeroInteractions(userService);
    }

    private Response performRequest(RegistrationRequestData registrationRequestData) {
        return resources.client().target("/register").request().post(Entity.json(registrationRequestData));
    }

}