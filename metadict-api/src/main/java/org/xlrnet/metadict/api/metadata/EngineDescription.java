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

/**
 * The interface {@link EngineDescription} is used to describe a search engine in a human readable format. Therefore the
 * description contains information like e.g. the name of the engine, the called website and copyright information.
 * New instances can be created by using the {@link EngineDescriptionBuilder}.
 */
public interface EngineDescription {

    /**
     * Returns the url to the website of the author.
     *
     * @return the url to the website of the author.
     */
    String getAuthorUrl();

    /**
     * Returns copyright information about the engine.
     *
     * @return copyright information about the engine.
     */
    String getCopyright();

    /**
     * Returns the name of the engine. This parameter has to be set always or the core might not load the engine.
     *
     * @return the name of the engine.
     */
    String getEngineName();

    /**
     * Returns a link to the engine's website. This link should contain either general information about the engine or
     * the full source code.
     *
     * @return a link to the engine's website.
     */
    String getEngineUrl();

    /**
     * Returns license information of the engine.
     *
     * @return license information of the engine.
     */
    String getLicense();

    /**
     * Returns copyright information about the underlying search backend.
     *
     * @return copyright information about the underlying search backend.
     */
    String getSearchBackendCopyright();

    /**
     * Returns a link to the search backend. This is the url of the underlying search engine that is being called.
     *
     * @return a link to the search backend.
     */
    String getSearchBackendLink();

    /**
     * Returns the name of the search backend. This is the name of the underlying search engine that is being called.
     *
     * @return the name of the search backend.
     */
    String getSearchBackendName();
}
