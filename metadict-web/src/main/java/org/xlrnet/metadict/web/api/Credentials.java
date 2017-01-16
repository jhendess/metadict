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

package org.xlrnet.metadict.web.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.web.auth.constraints.ValidPassword;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Credentials used for logging in to a webservice.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Credentials {

    @NotEmpty
    @JsonProperty
    @Size(min = ValidPassword.MINIMUM_PASSWORD_LENGTH, max = ValidPassword.MAXIMUM_PASSWORD_LENGTH)
    @Pattern(regexp = "[A-Za-z0-9_]+")
    private String name;

    @NotEmpty
    @JsonProperty
    private String password;

    public Credentials() {
    }

    public Credentials(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public String getPassword() {
        return this.password;
    }
}
