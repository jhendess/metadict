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

package org.xlrnet.metadict.web.auth.services;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.xlrnet.metadict.api.auth.User;
import org.xlrnet.metadict.api.storage.StorageService;
import org.xlrnet.metadict.core.services.storage.InMemoryStorage;
import org.xlrnet.metadict.web.auth.entities.*;
import org.xlrnet.metadict.web.middleware.services.SequenceService;
import org.xlrnet.metadict.web.middleware.util.CryptoUtils;

import java.util.Optional;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private static final String TEST_USER_NAME = "testUser";

    private static final String TEST_PASSWORD = "testPassword";

    private StorageService storageService;

    private UserService userService;

    private UserFactory userFactory;

    @Before
    public void setup() {
        this.storageService = spy(new InMemoryStorage());
        this.userFactory = new UserFactory(new SequenceService());
        this.userService = spy(new UserService(this.storageService, this.userFactory));
    }

    @Test
    public void testCreateNewUserWithPassword() throws Exception {
        Optional<User> newUserWithPassword = this.userService.createNewUser(TEST_USER_NAME, TEST_PASSWORD);

        assertTrue(newUserWithPassword.isPresent());
        User user = newUserWithPassword.get();
        assertEquals("testUser", user.getName());

        assertNotNull(user.getRoles());
        assertTrue("Roles must be empty", user.getRoles().isEmpty());
        assertTrue("User id may not be empty", StringUtils.isNotEmpty(user.getId()));
    }

    @Test
    public void testCreateNewUserWithPassword_existing() throws Exception {
        Optional<User> existingUser = Optional.of(this.userFactory.newDefaultUser(TEST_USER_NAME));
        doReturn(existingUser).when(this.userService).findUserDataByName(TEST_USER_NAME);

        Optional<User> newUser = this.userService.createNewUser(TEST_USER_NAME, TEST_PASSWORD);
        assertFalse("New user must be non-existing", newUser.isPresent());
        verify(this.storageService, never()).create(anyString(), anyString(), anyString());
        verify(this.storageService, never()).put(anyString(), anyString(), anyString());
    }

    @Test
    public void testAuthenticateWithPassword_correct() throws Exception {
        prepareAuthDataMock();
        Optional<User> user = this.userService.authenticateWithPassword(TEST_USER_NAME, TEST_PASSWORD);

        assertTrue("User should be present but isn't (i.e. password check doesn't work)", user.isPresent());
    }

    @Test
    public void testAuthenticateWithPassword_wrong() throws Exception {
        prepareAuthDataMock();
        Optional<User> user = this.userService.authenticateWithPassword(TEST_USER_NAME, "Some_Wrong_Password");

        assertFalse("User shouldn't be present but is (i.e. password check doesn't work)", user.isPresent());
    }

    @Test
    public void testHasRole_false() throws Exception {
        User user = this.userFactory.newDefaultUser(TEST_USER_NAME);
        boolean b = this.userService.hasRole(user, Roles.ADMIN_ROLE_ID);
        assertFalse("New user may not have role " + Roles.ADMIN_ROLE_ID, b);
    }

    @Test
    public void testHasRole_true() throws Exception {
        User user = new BasicUser("", TEST_USER_NAME, ImmutableSet.of(UserRole.ADMIN));
        boolean b = this.userService.hasRole(user, Roles.ADMIN_ROLE_ID);
        assertTrue("User must have role " + Roles.ADMIN_ROLE_ID + " but hasn't", b);
    }

    private void prepareAuthDataMock() throws org.xlrnet.metadict.api.storage.StorageBackendException, org.xlrnet.metadict.api.storage.StorageOperationException {
        byte[] salt = CryptoUtils.generateRandom(CryptoUtils.DEFAULT_SALT_LENGTH);
        byte[] hashedPassword = this.userService.hashPassword(TEST_PASSWORD, salt);
        Optional<BasicAuthData> authData = Optional.of(new BasicAuthData(hashedPassword, salt));
        Optional<User> user = Optional.of(this.userFactory.newDefaultUser(TEST_USER_NAME));


        doReturn(authData).when(this.storageService).read(UserService.BASIC_AUTH_NAMESPACE, TEST_USER_NAME, BasicAuthData.class);
        doReturn(authData).when(this.storageService).read(UserService.GENERAL_USER_NAMESPACE, TEST_USER_NAME, User.class);
    }
}