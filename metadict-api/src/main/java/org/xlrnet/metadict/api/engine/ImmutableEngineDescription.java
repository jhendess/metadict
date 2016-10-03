/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
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

package org.xlrnet.metadict.api.engine;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Immutable implementation for {@link EngineDescription}.
 */
public class ImmutableEngineDescription implements EngineDescription {

    private static final long serialVersionUID = -7529741892355637801L;

    protected final String authorName;

    protected final String authorLink;

    protected final String copyright;

    protected final String license;

    protected final String engineName;

    protected final String engineLink;

    protected final String searchBackendName;

    protected final String searchBackendLink;

    protected final String searchBackendCopyright;

    /**
     * Create a new immutable instance. See {@link EngineDescription} for information about the parameters.
     *
     * @param authorName
     *         Tame of the author.
     * @param authorLink
     *         Website of the author.
     * @param copyright
     *         Copyright for the engine.
     * @param license
     *         License for the engine.
     * @param engineName
     *         Name of the engine.
     * @param engineLink
     *         Website of the engine
     * @param searchBackendName
     *         Name of the search backend.
     * @param searchBackendLink
     *         Website of the search backend.
     * @param searchBackendCopyright
     *         Copyright for the search backend.
     */
    ImmutableEngineDescription(String authorName, String authorLink, String copyright, String license, String engineName, String engineLink, String searchBackendName, String searchBackendLink, String searchBackendCopyright) {
        this.authorName = authorName;
        this.authorLink = authorLink;
        this.copyright = copyright;
        this.license = license;
        this.engineName = engineName;
        this.engineLink = engineLink;
        this.searchBackendName = searchBackendName;
        this.searchBackendLink = searchBackendLink;
        this.searchBackendCopyright = searchBackendCopyright;
    }

    /**
     * Return a new builder instance for creating new {@link EngineDescription} objects.
     *
     * @return a new builder.
     */
    public static EngineDescriptionBuilder builder() {
        return new EngineDescriptionBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableEngineDescription)) return false;
        ImmutableEngineDescription that = (ImmutableEngineDescription) o;
        return Objects.equal(authorName, that.authorName) &&
                Objects.equal(authorLink, that.authorLink) &&
                Objects.equal(copyright, that.copyright) &&
                Objects.equal(license, that.license) &&
                Objects.equal(engineName, that.engineName) &&
                Objects.equal(engineLink, that.engineLink) &&
                Objects.equal(searchBackendName, that.searchBackendName) &&
                Objects.equal(searchBackendLink, that.searchBackendLink) &&
                Objects.equal(searchBackendCopyright, that.searchBackendCopyright);
    }

    public String getAuthorLink() {
        return authorLink;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getBackendCopyright() {
        return searchBackendCopyright;
    }

    public String getBackendLink() {
        return searchBackendLink;
    }

    public String getBackendName() {
        return searchBackendName;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getEngineLink() {
        return engineLink;
    }

    public String getEngineName() {
        return engineName;
    }

    public String getLicense() {
        return license;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authorName, authorLink, copyright, license, engineName, engineLink, searchBackendName, searchBackendLink, searchBackendCopyright);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("authorName", authorName)
                .add("authorLink", authorLink)
                .add("copyright", copyright)
                .add("license", license)
                .add("engineName", engineName)
                .add("engineLink", engineLink)
                .add("searchBackendName", searchBackendName)
                .add("searchBackendLink", searchBackendLink)
                .add("searchBackendCopyright", searchBackendCopyright)
                .toString();
    }
}
