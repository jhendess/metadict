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

import java.util.Collection;

/**
 * This interface describes a container that can be used to store a group of synonyms for a certain base meaning
 * of a word.
 * <p>
 * Example: The word house can have a synonym group for the meaning as a building, but another group for the meaning of
 * a royal house (e.g. the royal house).
 */
public interface SynonymGroup {

    /**
     * Returns the base meaning that all objects in this synonym group resemble.
     *
     * @return the base meaning that all objects in this synonym group resemble.
     */
    DictionaryObject getBaseMeaning();

    /**
     * Returns a collection of objects that are synonyms for the base meaning of this group.
     *
     * @return a collection of objects that are synonyms for the base meaning of this group.
     */
    Collection<DictionaryObject> getSynonyms();

}
