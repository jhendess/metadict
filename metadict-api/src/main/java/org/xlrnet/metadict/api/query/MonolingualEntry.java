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

import org.jetbrains.annotations.NotNull;

/**
 * The {@link MonolingualEntry} interface represents a single monolingual result entry of a query operation. Use the
 * {@link MonolingualEntryBuilder} class to build a new entry.
 * <p>
 * Each {@link MonolingualEntry} contains a single {@link DictionaryObject} object. These objects contain the
 * respective source or target translation of a word or phrase and additional information about use cases, grammatical
 * forms, etc.
 */
public interface MonolingualEntry extends Entry {

    /**
     * Get the entry's main data object. This object doesn't  have to correspond exactly to the original input query,
     * but should should be as similar as possible. The value may never be null.
     *
     * @return the main data object of this entry.
     */
    @NotNull
    DictionaryObject getContent();

}
