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

    /** Name of the author who developed the engine. */
    protected final String authorName;

    /** Link to a website of the author who developed the engine. */
    protected final String authorLink;

    /** Copyright for the engine. */
    protected final String copyright;

    /** License of the engine. */
    protected final String license;

    /** Name of the engine. */
    protected final String engineName;

    /** Link to the website of the engine. */
    protected final String engineLink;

    /** Name of the search backend. */
    protected final String searchBackendName;

    /** Link to the search backend. */
    protected final String searchBackendLink;

    /** Copyright for the search backend. */
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImmutableEngineDescription)) {
            return false;
        }
        ImmutableEngineDescription that = (ImmutableEngineDescription) o;
        return Objects.equal(this.authorName, that.authorName) &&
                Objects.equal(this.authorLink, that.authorLink) &&
                Objects.equal(this.copyright, that.copyright) &&
                Objects.equal(this.license, that.license) &&
                Objects.equal(this.engineName, that.engineName) &&
                Objects.equal(this.engineLink, that.engineLink) &&
                Objects.equal(this.searchBackendName, that.searchBackendName) &&
                Objects.equal(this.searchBackendLink, that.searchBackendLink) &&
                Objects.equal(this.searchBackendCopyright, that.searchBackendCopyright);
    }

    @Override
    public String getAuthorLink() {
        return this.authorLink;
    }

    @Override
    public String getAuthorName() {
        return this.authorName;
    }

    @Override
    public String getBackendCopyright() {
        return this.searchBackendCopyright;
    }

    @Override
    public String getBackendLink() {
        return this.searchBackendLink;
    }

    @Override
    public String getBackendName() {
        return this.searchBackendName;
    }

    @Override
    public String getCopyright() {
        return this.copyright;
    }

    @Override
    public String getEngineLink() {
        return this.engineLink;
    }

    @Override
    public String getEngineName() {
        return this.engineName;
    }

    @Override
    public String getLicense() {
        return this.license;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.authorName, this.authorLink, this.copyright, this.license, this.engineName, this.engineLink, this.searchBackendName, this.searchBackendLink, this.searchBackendCopyright);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("authorName", this.authorName)
                .add("authorLink", this.authorLink)
                .add("copyright", this.copyright)
                .add("license", this.license)
                .add("engineName", this.engineName)
                .add("engineLink", this.engineLink)
                .add("searchBackendName", this.searchBackendName)
                .add("searchBackendLink", this.searchBackendLink)
                .add("searchBackendCopyright", this.searchBackendCopyright)
                .toString();
    }
}
