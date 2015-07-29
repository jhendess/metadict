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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable implementation for {@link BilingualEntry}.
 */
public class ImmutableBilingualEntry extends AbstractEntry implements BilingualEntry {

    private static final long serialVersionUID = -1482979366776782055L;

    private final DictionaryObject inputObject;

    private final DictionaryObject outputObject;

    /**
     * Create a new immutable instance. See {@link BilingualEntry} for more information about the parameters.
     *
     * @param inputObject
     *         The dictionary object in input language.
     * @param outputObject
     *         The dictionary object in target language
     * @param entryType
     *         The type of this entry.
     */
    ImmutableBilingualEntry(DictionaryObject inputObject, DictionaryObject outputObject, EntryType entryType) {
        super(entryType);
        this.inputObject = inputObject;
        this.outputObject = outputObject;
    }

    /**
     * Return a new builder instance for creating new {@link BilingualEntry} objects.
     *
     * @return a new builder.
     */
    public static BilingualEntryBuilder builder() {
        return new BilingualEntryBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableBilingualEntry)) return false;
        ImmutableBilingualEntry that = (ImmutableBilingualEntry) o;
        return Objects.equal(inputObject, that.inputObject) &&
                Objects.equal(outputObject, that.outputObject) &&
                Objects.equal(entryType, that.entryType);
    }

    /**
     * Get the entry's {@link DictionaryObject} that contains information in the input language. This object doesn't
     * have to correspond exactly to the original input query, but should be as similar as possible. If the entry
     * originates from a one-language dictionary, this method has to return the word's meaning.
     *
     * @return the {@link DictionaryObject} in input language.
     */
    @NotNull
    @Override
    public DictionaryObject getInput() {
        return this.inputObject;
    }

    /**
     * Get the entry's {@link DictionaryObject} that contains information in the output language. If the entry
     * originates from a one-language dictionary, the returned value may be null.
     *
     * @return the {@link DictionaryObject} in output language or null for one-language dictionaries.
     */
    @NotNull
    @Override
    public DictionaryObject getOutput() {
        return this.outputObject;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inputObject, outputObject, entryType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("inputObject", inputObject)
                .add("outputObject", outputObject)
                .add("entryType", entryType)
                .toString();
    }
}
