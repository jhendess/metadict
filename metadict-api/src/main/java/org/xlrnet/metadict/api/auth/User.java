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

package org.xlrnet.metadict.api.auth;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;

/**
 * Representation of a user account on metadict. User accounts are used to identify an individual that is using
 */
public interface User extends Serializable {

    /**
     * Returns the internal unique identifier of this user. This may be a technical key and should never be shown to the
     * end user.
     *
     * @return the internal unique identifer of this user.
     */
    @NotNull
    String getId();

    /**
     * Returns the externally displayed name of this user. The name may be changed anytime.
     *
     * @return the externally displayed name of this user.
     */
    @NotNull
    String getName();

    /**
     * Returns a list of roles which are granted to this user.
     *
     * @return a list of roles which are granted to this user.
     */
    @NotNull
    Set<Role> getRoles();
}
