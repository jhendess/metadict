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

import java.io.Serializable;
import java.util.Collection;

/**
 * Immutable implementation of {@link SynonymEntry}.
 */
public class ImmutableSynonymEntry implements SynonymEntry, Serializable {

    private static final long serialVersionUID = -5679900170593355370L;

    private final DictionaryObject baseObject;

    private final Collection<SynonymGroup> synonymGroups;

    private final EntryType baseEntryType;

    protected ImmutableSynonymEntry(DictionaryObject baseObject, Collection<SynonymGroup> synonymGroups, EntryType baseEntryType) {
        this.baseObject = baseObject;
        this.synonymGroups = synonymGroups;
        this.baseEntryType = baseEntryType;
    }

    /**
     * Return a new builder instance for creating new {@link SynonymEntry} objects.
     *
     * @return a new builder.
     */
    public static SynonymEntryBuilder builder() {
        return new SynonymEntryBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableSynonymEntry)) return false;
        ImmutableSynonymEntry that = (ImmutableSynonymEntry) o;
        return Objects.equal(baseObject, that.baseObject) &&
                Objects.equal(synonymGroups, that.synonymGroups);
    }

    @Override
    public EntryType getBaseEntryType() {
        return baseEntryType;
    }

    @Override
    public DictionaryObject getBaseObject() {
        return baseObject;
    }

    @Override
    public Collection<SynonymGroup> getSynonymGroups() {
        return synonymGroups;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(baseObject, synonymGroups);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("baseObject", baseObject)
                .add("synonymGroups", synonymGroups)
                .toString();
    }
}
