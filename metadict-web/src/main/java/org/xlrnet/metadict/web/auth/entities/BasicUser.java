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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.apache.commons.collections.set.UnmodifiableSet;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.auth.Role;
import org.xlrnet.metadict.api.auth.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link User} for basic authentication.
 */
public class BasicUser implements User {

    private static final long serialVersionUID = 1066514268054405602L;

    private String id;

    private String name;

    private Set<Role> roles;

    public BasicUser() {
    }

    public BasicUser(@NotNull String id, @NotNull String name, @NotNull Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.roles = new HashSet<>(roles.size());
        this.setRoles(roles);
    }

    @NotNull
    @Override
    public String getId() {
        return this.id;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @NotNull
    @Override
    public Set<Role> getRoles() {
        return UnmodifiableSet.decorate(this.roles);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoles(Set<Role> roles) {
        this.roles.addAll(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasicUser)) {
            return false;
        }
        BasicUser basicUser = (BasicUser) o;
        return Objects.equal(this.id, basicUser.id) &&
                Objects.equal(this.name, basicUser.name) &&
                Objects.equal(this.roles, basicUser.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id, this.name, this.roles);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", this.id)
                .add("name", this.name)
                .add("roles", this.roles)
                .toString();
    }
}
