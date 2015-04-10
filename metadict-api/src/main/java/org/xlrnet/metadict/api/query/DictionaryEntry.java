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

/**
 * The {@link DictionaryEntry} interface represents a single result entry of a query operation. Use the {@link
 * DictionaryEntryBuilder} class to build a new entry. Multiple entries should be grouped together using a {@link
 * EngineQueryResult}.
 * <p>
 * Each {@link DictionaryEntry} consists of two different values: an input and an output content. Both objects must
 * implement the {@link DictionaryObject} interface. These objects contain the respective source or target translation
 * of a word or phrase and additional information about use cases, grammatical forms, etc.
 */
public interface DictionaryEntry {

    /**
     * Get the entry's type. In most cases this is similar to a word class like nouns or verbs. However, you can also
     * provide phrases by using {@link EntryType#PHRASE}.
     *
     * @return the entry's type (i.e. word class in most cases).
     */
    EntryType getEntryType();

    /**
     * Get the entry's {@link DictionaryObject} that contains information in the input language. This object doesn't
     * have to correspond exactly to the original input query, but be should as similar as possible. If the entry
     * originates from a one-language dictionary, this method has to return the word's meaning. The value may never be
     * null.
     *
     * @return the {@link DictionaryObject} in input language.
     */
    DictionaryObject getInput();

    /**
     * Get the entry's {@link DictionaryObject} that contains information in the output language. If the entry
     * originates from a one-language dictionary, the returned value may be null.
     *
     * @return the {@link DictionaryObject} in output language or null for one-language dictionaries.
     */
    DictionaryObject getOutput();

}
