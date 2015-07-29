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

/**
 * Builder for creating new {@link EngineDescription} objects.
 */
public class EngineDescriptionBuilder {

    private String authorName;

    private String authorLink;

    private String copyright;

    private String license;

    private String engineName;

    private String engineUrl;

    private String searchBackendName;

    private String searchBackendLink;

    private String searchBackendCopyright;

    EngineDescriptionBuilder() {
        
    }
    
    /**
     * Create a new instance of {@link EngineDescription}.
     *
     * @return a new instance of {@link EngineDescription}.
     */
    public EngineDescription build() {
        return new ImmutableEngineDescription(authorName, authorLink, copyright, license, engineName, engineUrl, searchBackendName, searchBackendLink, searchBackendCopyright);
    }

    public EngineDescriptionBuilder setAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public EngineDescriptionBuilder setAuthorLink(String authorLink) {
        this.authorLink = authorLink;
        return this;
    }

    public EngineDescriptionBuilder setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    public EngineDescriptionBuilder setEngineName(String engineName) {
        this.engineName = engineName;
        return this;
    }

    public EngineDescriptionBuilder setEngineUrl(String engineUrl) {
        this.engineUrl = engineUrl;
        return this;
    }

    public EngineDescriptionBuilder setLicense(String license) {
        this.license = license;
        return this;
    }

    public EngineDescriptionBuilder setSearchBackendCopyright(String searchBackendCopyright) {
        this.searchBackendCopyright = searchBackendCopyright;
        return this;
    }

    public EngineDescriptionBuilder setSearchBackendLink(String searchBackendLink) {
        this.searchBackendLink = searchBackendLink;
        return this;
    }

    public EngineDescriptionBuilder setSearchBackendName(String searchBackendName) {
        this.searchBackendName = searchBackendName;
        return this;
    }
}
