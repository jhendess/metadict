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
import org.xlrnet.metadict.api.storage.StorageBackendException;
import org.xlrnet.metadict.web.auth.dao.UserAccess;
import org.xlrnet.metadict.web.auth.entities.PersistedUser;
import org.xlrnet.metadict.web.auth.entities.BasicUser;
import org.xlrnet.metadict.web.auth.entities.Roles;
import org.xlrnet.metadict.web.auth.entities.UserRole;
import org.xlrnet.metadict.web.auth.entities.factories.UserFactory;
import org.xlrnet.metadict.web.middleware.services.SequenceService;
import org.xlrnet.metadict.web.middleware.util.CryptoUtils;
import org.xlrnet.metadict.web.util.ConversionUtils;

import javax.xml.bind.DatatypeConverter;
import java.util.Optional;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private static final String TEST_USER_NAME = "testUser";

    private static final String TEST_PASSWORD = "testPassword";

    private UserService userService;

    private UserFactory userFactory;

    private UserAccess userAccess;

    @Before
    public void setup() {
        this.userFactory = new UserFactory(new SequenceService());
        this.userAccess = mock(UserAccess.class);
        this.userService = spy(new UserService(this.userFactory, userAccess));
    }

    @Test
    public void testCreateNewUserWithPassword() throws Exception {
        Optional<User> newUserWithPassword = this.userService.createNewUser(TEST_USER_NAME, TEST_PASSWORD);

        assertTrue(newUserWithPassword.isPresent());
        User user = newUserWithPassword.get();
        assertEquals("testUser", user.getName());

        assertNotNull(user.getRoles());
        assertTrue("User must have REGULAR role", user.getRoles().contains(UserRole.REGULAR_USER));
        assertTrue("User id may not be empty", StringUtils.isNotEmpty(user.getId()));

        verify(this.userAccess).persist(any(PersistedUser.class));
    }

    @Test
    public void testCreateNewTechUserWithPassword() throws Exception {
        User user = this.userService.createTechnicalUser(TEST_PASSWORD);

        assertNotNull(user);
        assertTrue("User name may not be empty", StringUtils.isNotEmpty(user.getName()));

        assertNotNull(user.getRoles());
        assertTrue("User must have TECHNICAL role", user.getRoles().contains(UserRole.TECH_USER));
        assertTrue("User id may not be empty", StringUtils.isNotEmpty(user.getId()));

        verify(this.userAccess).persist(any(PersistedUser.class));
    }

    @Test
    public void testCreateNewUserWithPassword_existing() throws Exception {
        Optional<User> existingUser = Optional.of(this.userFactory.newDefaultUser(TEST_USER_NAME));
        doReturn(existingUser).when(this.userService).findUserDataByName(TEST_USER_NAME);

        Optional<User> newUser = this.userService.createNewUser(TEST_USER_NAME, TEST_PASSWORD);
        assertFalse("New user must be non-existing", newUser.isPresent());
        verify(this.userAccess, never()).persist(any(PersistedUser.class));
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

    @Test
    public void testRemoveUser() throws StorageBackendException {
        // Prepare user for test
        User user = this.userService.createTechnicalUser(TEST_PASSWORD);
        assertTrue("User should exist, but doesn't", this.userService.findUserDataByName(user.getName()).isPresent());

        // Remove the user
        boolean status = this.userService.removeUser(user.getName());

        assertTrue("Removing user should have been successful", status);
        assertFalse("User shouldn't exist, but does", this.userService.findUserDataByName(user.getName()).isPresent());

        verify(this.userAccess).deleteByName(user.getName());
    }

    @Test
    public void testRemoveUser_failed() throws StorageBackendException {
        String username = DatatypeConverter.printHexBinary(CryptoUtils.generateRandom(16));

        // Remove the user
        boolean status = this.userService.removeUser(username);

        assertFalse("Removing user should fail, but didn't", status);
    }

    private void prepareAuthDataMock() {
        String salt = ConversionUtils.byteArrayToHexString(CryptoUtils.generateRandom(CryptoUtils.DEFAULT_SALT_LENGTH));
        String hashedPassword = this.userService.hashPassword(TEST_PASSWORD, salt);
        PersistedUser persistedUser = new PersistedUser();
        persistedUser.setId("123");
        persistedUser.setName(TEST_USER_NAME);
        persistedUser.setSalt(salt);
        persistedUser.setPassword(hashedPassword);

        doReturn(persistedUser).when(userAccess).findByName(TEST_USER_NAME);
    }
}