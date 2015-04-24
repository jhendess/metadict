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

package org.xlrnet.metadict.core.exception;

/**
 * The {@link UnknownSearchEngineException} will be thrown, when an unknown {@link
 * org.xlrnet.metadict.api.engine.SearchEngine} was requested. This should indicate a severe programming error that
 * should only be noticed during development, therefore this is only a {@link RuntimeException}.
 */
public class UnknownSearchEngineException extends RuntimeException {

    /**
     * Construct a new {@link UnknownSearchEngineException} with the given String as the name of the failed {@link
     * org.xlrnet.metadict.api.engine.SearchEngine}.
     *
     * @param engineName
     *         name of the failed search engine
     */
    public UnknownSearchEngineException(String engineName) {
        super("Couldn't find unknown search engine " + engineName);
    }

}
