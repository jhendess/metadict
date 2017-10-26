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

package org.xlrnet.metadict.web.auth.entities.factories;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.auth.Role;
import org.xlrnet.metadict.api.auth.User;
import org.xlrnet.metadict.web.auth.db.entities.PersistedUser;
import org.xlrnet.metadict.web.auth.entities.BasicUser;
import org.xlrnet.metadict.web.auth.entities.UserRole;
import org.xlrnet.metadict.web.middleware.services.SequenceService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory for creating new {@link org.xlrnet.metadict.api.auth.User} objects.
 */
public class UserFactory {

    private final SequenceService sequenceService;

    private static final Set<Role> DEFAULT_TECHNICAL_ROLES = ImmutableSet.of(UserRole.TECH_USER);

    private static final Set<Role> DEFAULT_USER_ROLES = ImmutableSet.of(UserRole.REGULAR_USER);

    @Inject
    public UserFactory(SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    /**
     * Creates a new regular user account.
     *
     * @param username
     *         The username for the account.
     * @param additionalRoles
     *         Additional roles for the new account besides {@link UserRole#REGULAR_USER}.
     * @return A new user.
     */
    @NotNull
    public User newDefaultUser(@NotNull String username, Role... additionalRoles) {
        return internalNewUser(username, DEFAULT_USER_ROLES, additionalRoles);
    }

    /**
     * Creates a new technical user account.
     *
     * @param username
     *         The username for the account.
     * @param additionalRoles
     *         Additional roles for the new account besides {@link UserRole#REGULAR_USER}.
     * @return A new technical user.
     */
    @NotNull
    public User newTechnicalUser(@NotNull String username, Role... additionalRoles) {
        return internalNewUser(username, DEFAULT_TECHNICAL_ROLES, additionalRoles);
    }

    @NotNull
    private User internalNewUser(@NotNull String username, Set<Role> defaultRoles, Role... additionalRoles) {
        String uuid = sequenceService.newUUIDString();
        Set<Role> newRoles = new HashSet<>();
        newRoles.addAll(defaultRoles);
        Collections.addAll(newRoles, additionalRoles);
        return new BasicUser(uuid, username, newRoles);
    }

    /**
     * Creates a new {@link User} from a persisted {@link PersistedUser} entity.
     * @param persistedUser The persisted from which a user should be created.
     * @return A new user instance.
     */
    @NotNull
    public User fromPersistedEntity(@NotNull PersistedUser persistedUser) {
        return new BasicUser(persistedUser.getId(), persistedUser.getName(), persistedUser.getRoles());
    }
}
