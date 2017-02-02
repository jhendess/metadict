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

package org.xlrnet.metadict.web.auth.entities;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.auth.Role;
import org.xlrnet.metadict.api.auth.User;
import org.xlrnet.metadict.web.middleware.services.SequenceService;

import javax.inject.Inject;
import java.util.List;

/**
 * Factory for creating new {@link org.xlrnet.metadict.api.auth.User} objects.
 */
public class UserFactory {

    private final SequenceService sequenceService;

    private static final List<Role> DEFAULT_ROLES = ImmutableList.of();

    @Inject
    public UserFactory(SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    @NotNull
    public User newDefaultUser(@NotNull String username) {
        String uuid = sequenceService.newUUIDString();
        return new BasicUser(uuid, username, DEFAULT_ROLES);
    }
}
