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

    private final DictionaryObject sourceObject;

    private final DictionaryObject targetObject;

    /**
     * Create a new immutable instance. See {@link BilingualEntry} for more information about the parameters.
     *
     * @param sourceObject
     *         The dictionary object in input language.
     * @param targetObject
     *         The dictionary object in target language
     * @param entryType
     *         The type of this entry.
     */
    ImmutableBilingualEntry(DictionaryObject sourceObject, DictionaryObject targetObject, EntryType entryType) {
        super(entryType);
        this.sourceObject = sourceObject;
        this.targetObject = targetObject;
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
        return Objects.equal(sourceObject, that.sourceObject) &&
                Objects.equal(targetObject, that.targetObject) &&
                Objects.equal(entryType, that.entryType);
    }

    /**
     * @inheritDoc
     */
    @NotNull
    @Override
    public DictionaryObject getSource() {
        return this.sourceObject;
    }

    /**
     * @inheritDoc
     */
    @NotNull
    @Override
    public DictionaryObject getTarget() {
        return this.targetObject;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sourceObject, targetObject, entryType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sourceObject", sourceObject)
                .add("targetObject", targetObject)
                .add("entryType", entryType)
                .toString();
    }
}
