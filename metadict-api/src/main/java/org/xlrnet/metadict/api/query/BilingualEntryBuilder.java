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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for creating new {@link BilingualEntry} objects.
 */
public class BilingualEntryBuilder {

    private DictionaryObject inputObject;

    private DictionaryObject outputObject;

    private EntryType entryType = EntryType.UNKNOWN;

    /**
     * Build a new instance of {@link BilingualEntry} with the set properties.
     *
     * @return a new instance of {@link BilingualEntry}.
     */
    public BilingualEntry build() {
        return new BilingualEntryImpl(inputObject, outputObject, entryType);
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
    public BilingualEntryBuilder setEntryType(EntryType entryType) {
        checkNotNull(entryType);

        this.entryType = entryType;
        return this;
    }

    /**
     * Set the entry's {@link DictionaryObject} that contains information in the input language. This object doesn't
     * have to correspond exactly to the original input query, but be should as similar as possible. This value has to
     * be set always.
     *
     * @param inputObject
     *         The {@link DictionaryObject} in input language.
     * @return this builder instance.
     */
    public BilingualEntryBuilder setInputObject(DictionaryObject inputObject) {
        checkNotNull(inputObject);

        this.inputObject = inputObject;
        return this;
    }

    /**
     * Set the entry's {@link DictionaryObject} that contains information in the output language. If the entry
     * originates from a one-language dictionary, this value can be unset.
     *
     * @param outputObject  the {@link DictionaryObject} in output language or null for one-language dictionaries.
     * @return this builder instance.
     */
    public BilingualEntryBuilder setOutputObject(DictionaryObject outputObject) {
        checkNotNull(outputObject);

        this.outputObject = outputObject;
        return this;
    }
}
