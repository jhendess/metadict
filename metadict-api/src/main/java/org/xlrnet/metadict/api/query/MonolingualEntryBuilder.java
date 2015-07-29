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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for creating new {@link MonolingualEntry} objects.
 */
public class MonolingualEntryBuilder {

    private DictionaryObject content;

    private EntryType entryType = EntryType.UNKNOWN;

    MonolingualEntryBuilder() {

    }

    /**
     * Build a new instance of {@link BilingualEntry} with the set properties.
     *
     * @return a new instance of {@link BilingualEntry}.
     */
    public MonolingualEntry build() {
        return new ImmutableMonolingualEntry(entryType, content);
    }

    /**
     * Set the entry's type. In most cases this is similar to a word class like nouns or verbs. However, you can also
     * provide phrases by using {@link EntryType#PHRASE}. If no type is defined, the type will default to {@link
     * EntryType#UNKNOWN}.
     *
     * @param entryType
     *         the entry's type (i.e. word class in most cases).
     * @return this builder instance.
     */
    @NotNull
    public MonolingualEntryBuilder setEntryType(@NotNull EntryType entryType) {
        checkNotNull(entryType);

        this.entryType = entryType;
        return this;
    }

    /**
     * Set the entry's main data object. This object doesn't have to correspond exactly to the original input query,
     * but should should be as similar as possible. The value may never be null.
     *
     * @param content
     *         The {@link DictionaryObject} in input language.
     * @return this builder instance.
     */
    @NotNull
    public MonolingualEntryBuilder setContent(@NotNull DictionaryObject content) {
        checkNotNull(content);

        this.content = content;
        return this;
    }
}
