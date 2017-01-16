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

package org.xlrnet.metadict.web.auth;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.auth.Role;
import org.xlrnet.metadict.api.auth.User;
import org.xlrnet.metadict.api.exception.MetadictRuntimeException;
import org.xlrnet.metadict.api.storage.StorageBackendException;
import org.xlrnet.metadict.api.storage.StorageOperationException;
import org.xlrnet.metadict.api.storage.StorageService;
import org.xlrnet.metadict.core.services.storage.DefaultStorageService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Central service for user management. This includes creating new users and authenticating them.
 */
public class UserService {

    /**
     * Name of the namespace which contains basic authentication data.
     */
    static final String BASIC_AUTH_NAMESPACE = "AUTH_BASIC";

    /**
     * General user namespace.
     */
    static final String GENERAL_USER_NAMESPACE = "USERS";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private static ReentrantLock createUserLock = new ReentrantLock();

    private final StorageService storageService;

    private final UserFactory userFactory;

    @Inject
    public UserService(@DefaultStorageService StorageService storageService, UserFactory userFactory) {
        this.storageService = storageService;
        this.userFactory = userFactory;
    }

    @NotNull
    public Optional<User> createNewUser(@NotNull String username, @NotNull String unhashedPassword) {
        checkNotNull(username, "Username may not be null");
        checkNotNull(unhashedPassword, "Password may not be null");

        User user = null;

        // Lock user account creation to avoid race conditions
        createUserLock.lock(); // TODO: Replace this with a more stable solution which supports better concurrency

        try {
            Optional<User> userDataByName = findUserDataByName(username);

            if (!userDataByName.isPresent()) {
                LOGGER.info("Creating new user {}", username);

                byte[] salt = CryptoUtils.generateRandom(CryptoUtils.DEFAULT_SALT_LENGTH);
                byte[] hashedPassword = hashPassword(unhashedPassword, salt);

                BasicAuthData basicAuthData = new BasicAuthData(hashedPassword, salt);
                user = this.userFactory.newDefaultUser(username);

                try {
                    this.storageService.create(BASIC_AUTH_NAMESPACE, username, basicAuthData);
                    this.storageService.create(GENERAL_USER_NAMESPACE, username, user);
                    LOGGER.info("Created new user {}", username);
                } catch (StorageBackendException | StorageOperationException e) {
                    LOGGER.error("Unexpected error while creating new user", e);
                }
            }
        } finally {
            createUserLock.unlock();
        }

        return Optional.ofNullable(user);
    }

    /**
     * Try to authenticate a user with a given username and return the authenticated {@link User} ifcorret.
     *
     * @param username
     *         The user which should be authenticated.
     * @param unhashedPassword
     *         The unhashed password of the user.
     * @return An {@link Optional} containing the user if authentication was successful. An empty optional if the
     * authentication failed.
     */
    @NotNull
    public Optional<User> authenticateWithPassword(@NotNull String username, @NotNull String unhashedPassword) {
        checkNotNull(username, "Username may not be null");
        checkNotNull(unhashedPassword, "Password may not be null");

        Optional<User> user = Optional.empty();

        try {
            Optional<BasicAuthData> hashedData = this.storageService.read(BASIC_AUTH_NAMESPACE, username, BasicAuthData.class);
            if (hashedData.isPresent()) {
                BasicAuthData basicAuthData = hashedData.get();
                byte[] hashPassword = hashPassword(unhashedPassword, basicAuthData.getSalt());
                if (Arrays.equals(basicAuthData.getHashedPassword(), hashPassword)) {
                    LOGGER.debug("Successfully authenticated user {}", username);
                    user = findUserDataByName(username);
                } else {
                    LOGGER.debug("Authentication failed for user {}", username);
                }
            } else {
                LOGGER.debug("No authentication data found for user {}", username);
            }
        } catch (StorageBackendException | StorageOperationException e) {
            LOGGER.error("An unexpected error occured while trying to authenticate a user", e);
            throw new MetadictRuntimeException(e);
        }

        return user;
    }

    /**
     * Checks if the given user has the given role id.
     *
     * @param user
     *         The user to check.
     * @param roleId
     *         The role to check.
     * @return True if the user has the given role or false.
     */
    public boolean hasRole(User user, String roleId) {
        boolean hasRole = false;
        for (Role role : user.getRoles()) {
            if (StringUtils.equals(role.getId(), roleId)) {
                hasRole = true;
                break;
            }
        }
        return hasRole;
    }

    /**
     * Tries to find user data for a given user id.
     *
     * @param username
     *         The user id.
     * @return An {@link Optional} containing the user data or an empty optional if no user could be found.
     */
    @NotNull
    public Optional<User> findUserDataByName(@NotNull String username) {
        checkNotNull(username);

        try {
            Optional<User> read = this.storageService.read(GENERAL_USER_NAMESPACE, username, User.class);
            return read;
        } catch (StorageBackendException | StorageOperationException e) {
            LOGGER.error("An unexpected error occurred while trying to read user data", e);
            throw new MetadictRuntimeException(e);
        }
    }

    @NotNull
    byte[] hashPassword(@NotNull String unhashedPassword, @NotNull byte[] salt) {
        return CryptoUtils.hashPassword(unhashedPassword.toCharArray(), salt, CryptoUtils.DEFAULT_ITERATIONS, CryptoUtils.DEFAULT_KEYLENGTH);
    }
}
