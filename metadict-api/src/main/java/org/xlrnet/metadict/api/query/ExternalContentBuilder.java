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

package org.xlrnet.metadict.api.query;

import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for creating new {@link ExternalContent} objects.
 */
public class ExternalContentBuilder {

    private String title;

    private String description;

    private URL link;

    /**
     * Build a new instance of {@link ExternalContent} with the set properties.
     *
     * @return a new instance of {@link ExternalContent}.
     */
    public ExternalContent build() {
        checkNotNull(link, "Link to external content may not be null");

        return new ExternalContentImpl(title, description, link);
    }

    /**
     * Sets a description for the external content. This should be longer than the title of the content.
     *
     * @param description
     *         a description for the external content.
     * @return the current builder
     */
    public ExternalContentBuilder setDescription(String description) {
        checkNotNull(description, "ExternalContent description may not be null");

        this.description = description;
        return this;
    }

    /**
     * Sets the link to the external content.
     *
     * @param link
     *         the link to the external content.
     * @return the current builder
     */
    public ExternalContentBuilder setLink(URL link) {
        checkNotNull(link, "ExternalContent link may not be null");

        this.link = link;
        return this;
    }

    /**
     * Sets the title of the external content. This should be shorter than the content's description.
     *
     * @param title
     *         the title of the external content.
     * @return the current builder
     */
    public ExternalContentBuilder setTitle(String title) {
        checkNotNull(title, "ExternalContent title may not be null");

        this.title = title;
        return this;
    }

}
