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

import java.io.Serializable;

/**
 * Abstract entry implementation.
 */
public abstract class AbstractEntry implements Serializable {

    private static final long serialVersionUID = 1081814399702638748L;

    protected final EntryType entryType;

    protected AbstractEntry() {
        entryType = null;
    }

    public AbstractEntry(EntryType entryType) {
        this.entryType = entryType;
    }

    /**
     * Get the entry's type. In most cases this is similar to a word class like nouns or verbs. However, you can also
     * provide phrases by using {@link EntryType#PHRASE}.
     *
     * @return the entry's type (i.e. word class in most cases).
     */
    @NotNull
    public EntryType getEntryType() {
        return this.entryType;
    }
}
