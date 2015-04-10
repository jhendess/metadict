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

package org.xlrnet.metadict.impl.aggregation;

import org.xlrnet.metadict.api.query.DictionaryEntry;

/**
 * The {@link ResultEntry} class represents a single processed result entry from the query. This is basically like the
 * {@link DictionaryEntry} but also provides information about the source of the entry and scoring information.
 */
public interface ResultEntry extends DictionaryEntry {

    /**
     * Returns the calculated relevance score for this entry. The score should be a value between 0.0 and 1.0
     * (inclusive) where a value of 1.0 means best possible relevancy.
     *
     * @return the calculated relevance score for this entry.
     */
    double getEntryScore();

    /**
     * Returns the name of the engine that produced this entry.
     *
     * @return the name of the engine that produced this entry.
     */
    String getSourceEngine();

}
