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

package org.xlrnet.metadict.api.metadata;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Immutable implementation for {@link EngineDescription}.
 */
public class EngineDescriptionImpl implements EngineDescription {

    private static final long serialVersionUID = -7529741892355637801L;

    protected final String authorName;

    protected final String authorUrl;

    protected final String copyright;

    protected final String license;

    protected final String engineName;

    protected final String engineUrl;

    protected final String searchBackendName;

    protected final String searchBackendLink;

    protected final String searchBackendCopyright;

    /**
     * Create a new immutable instance. See {@link EngineDescription} for information about the parameters.
     *
     * @param authorName
     *         Tame of the author.
     * @param authorUrl
     *         Website of the author.
     * @param copyright
     *         Copyright for the engine.
     * @param license
     *         License for the engine.
     * @param engineName
     *         Name of the engine.
     * @param engineUrl
     *         Website of the engine
     * @param searchBackendName
     *         Name of the search backend.
     * @param searchBackendLink
     *         Website of the search backend.
     * @param searchBackendCopyright
     *         Copyright for the search backend.
     */
    EngineDescriptionImpl(String authorName, String authorUrl, String copyright, String license, String engineName, String engineUrl, String searchBackendName, String searchBackendLink, String searchBackendCopyright) {
        this.authorName = authorName;
        this.authorUrl = authorUrl;
        this.copyright = copyright;
        this.license = license;
        this.engineName = engineName;
        this.engineUrl = engineUrl;
        this.searchBackendName = searchBackendName;
        this.searchBackendLink = searchBackendLink;
        this.searchBackendCopyright = searchBackendCopyright;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EngineDescriptionImpl)) return false;
        EngineDescriptionImpl that = (EngineDescriptionImpl) o;
        return Objects.equal(authorName, that.authorName) &&
                Objects.equal(authorUrl, that.authorUrl) &&
                Objects.equal(copyright, that.copyright) &&
                Objects.equal(license, that.license) &&
                Objects.equal(engineName, that.engineName) &&
                Objects.equal(engineUrl, that.engineUrl) &&
                Objects.equal(searchBackendName, that.searchBackendName) &&
                Objects.equal(searchBackendLink, that.searchBackendLink) &&
                Objects.equal(searchBackendCopyright, that.searchBackendCopyright);
    }

    /**
     * Returns the name of the author who developed the engine.
     *
     * @return the name of the author who developed the engine.
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Returns the url to the website of the author.
     *
     * @return the url to the website of the author.
     */
    public String getAuthorUrl() {
        return authorUrl;
    }

    /**
     * Returns copyright information about the engine.
     *
     * @return copyright information about the engine.
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Returns the name of the engine. This parameter has to be set always or the core might not load the engine.
     *
     * @return the name of the engine.
     */
    public String getEngineName() {
        return engineName;
    }

    /**
     * Returns a link to the engine's website. This link should contain either general information about the engine or
     * the full source code.
     *
     * @return a link to the engine's website.
     */
    public String getEngineUrl() {
        return engineUrl;
    }

    /**
     * Returns license information of the engine.
     *
     * @return license information of the engine.
     */
    public String getLicense() {
        return license;
    }

    /**
     * Returns copyright information about the underlying search backend.
     *
     * @return copyright information about the underlying search backend.
     */
    public String getSearchBackendCopyright() {
        return searchBackendCopyright;
    }

    /**
     * Returns a link to the search backend. This is the url of the underlying search engine that is being called.
     *
     * @return a link to the search backend.
     */
    public String getSearchBackendLink() {
        return searchBackendLink;
    }

    /**
     * Returns the name of the search backend. This is the name of the underlying search engine that is being called.
     *
     * @return the name of the search backend.
     */
    public String getSearchBackendName() {
        return searchBackendName;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authorName, authorUrl, copyright, license, engineName, engineUrl, searchBackendName, searchBackendLink, searchBackendCopyright);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("authorName", authorName)
                .add("authorUrl", authorUrl)
                .add("copyright", copyright)
                .add("license", license)
                .add("engineName", engineName)
                .add("engineUrl", engineUrl)
                .add("searchBackendName", searchBackendName)
                .add("searchBackendLink", searchBackendLink)
                .add("searchBackendCopyright", searchBackendCopyright)
                .toString();
    }
}
