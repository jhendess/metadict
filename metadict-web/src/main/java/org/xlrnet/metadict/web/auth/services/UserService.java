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

import io.dropwizard.hibernate.UnitOfWork;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.auth.Role;
import org.xlrnet.metadict.api.auth.User;
import org.xlrnet.metadict.api.exception.MetadictRuntimeException;
import org.xlrnet.metadict.api.storage.StorageBackendException;
import org.xlrnet.metadict.web.auth.db.dao.UserAccess;
import org.xlrnet.metadict.web.auth.db.entities.PersistedUser;
import org.xlrnet.metadict.web.auth.entities.factories.UserFactory;
import org.xlrnet.metadict.web.middleware.util.CryptoUtils;
import org.xlrnet.metadict.web.util.ConversionUtils;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Function;

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

    /**
     * Default byte size of technical users should be 16. This will create hexadecimal user names with 32 characters.
     */
    private static final int TECHNICAL_USER_NAME_LENGTH = 16;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     * Factory for creating new users.
     */
    private final UserFactory userFactory;

    /**
     * Access component for user data.
     */
    private final UserAccess userAccess;

    @Inject
    public UserService(UserFactory userFactory, UserAccess userAccess) {
        this.userFactory = userFactory;
        this.userAccess = userAccess;
    }

    @NotNull
    public Optional<User> createNewUser(@NotNull String username, @NotNull String unhashedPassword, Role... additionalRoles) {
        User user = internalCreateNewUser(username, unhashedPassword, (String u) -> this.userFactory.newDefaultUser(u, additionalRoles));

        return Optional.ofNullable(user);
    }

    @Nullable
    private User internalCreateNewUser(@NotNull String username, @NotNull String unhashedPassword, Function<String, User> userSupplier) {
        checkNotNull(username, "Username may not be null");
        checkNotNull(unhashedPassword, "Password may not be null");

        User user = null;

        Optional<User> userDataByName = findUserDataByName(username);

        if (!userDataByName.isPresent()) {
            LOGGER.debug("Creating new user {}", username);
            user = userSupplier.apply(username);

            byte[] salt = CryptoUtils.generateRandom(CryptoUtils.DEFAULT_SALT_LENGTH);
            String saltString = ConversionUtils.byteArrayToHexString(salt);
            String hashedPassword = hashPassword(unhashedPassword, saltString);

            PersistedUser persistedUser = new PersistedUser();
            persistedUser.setId(user.getId());
            persistedUser.setName(user.getName());
            persistedUser.setRoles(user.getRoles());
            persistedUser.setPassword(hashedPassword);
            persistedUser.setSalt(saltString);

            userAccess.persist(persistedUser);
        }
        return user;
    }

    /**
     * Creates a new technical user with a random user name.
     *
     * @param unhashedPassword
     *         The unhashed password for the new technical user.
     * @return A new technical user.
     */
    @NotNull
    @UnitOfWork
    public User createTechnicalUser(@NotNull String unhashedPassword) {
        checkNotNull(unhashedPassword, "Password may not be null");

        byte[] bytes = CryptoUtils.generateRandom(TECHNICAL_USER_NAME_LENGTH);
        String randomUserName = ConversionUtils.byteArrayToHexString(bytes);
        User user = internalCreateNewUser(randomUserName, unhashedPassword, (String u) -> this.userFactory.newTechnicalUser(u));
        if (user == null) {
            throw new MetadictRuntimeException("New technical user was null");
        }
        return user;
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

        Optional<PersistedUser> optionalPersistedUser = userAccess.findByName(username);
        if (optionalPersistedUser.isPresent()) {
            PersistedUser persistedUser = optionalPersistedUser.get();
            String hashPassword = hashPassword(unhashedPassword, persistedUser.getSalt());
            if (StringUtils.equals(persistedUser.getPassword(), hashPassword)) {
                LOGGER.debug("Successfully authenticated user {}", username);
                user = findUserDataByName(username);
            } else {
                LOGGER.debug("Authentication failed for user {}", username);
            }
        } else {
            LOGGER.debug("No authentication data found for user {}", username);
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
     * Removes all data for the given username.
     *
     * @param username
     *         The name of the user to remove.
     * @return True if the user could be deleted, false if not.
     */
    public boolean removeUser(@NotNull String username) throws StorageBackendException {
        boolean result = userAccess.deleteByName(username);

        if (result) {
            LOGGER.info("Deleted user {}", username);
        } else {
            LOGGER.debug("Tried to remove non-existing user {}", username);
        }

        return result;
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
        Optional<PersistedUser> userByName = userAccess.findByName(username);
        if (userByName.isPresent()) {
            PersistedUser persistedUser = userByName.get();
            return Optional.of(userFactory.fromPersistedEntity(persistedUser));
        }
        return Optional.empty();
    }

    @NotNull
    String hashPassword(@NotNull String unhashedPassword, @NotNull String salt) {
        byte[] bytes = CryptoUtils.hashPassword(unhashedPassword.toCharArray(), ConversionUtils.hexStringToByteArray(salt), CryptoUtils.DEFAULT_ITERATIONS, CryptoUtils.DEFAULT_KEYLENGTH);
        return ConversionUtils.byteArrayToHexString(bytes);
    }
}
