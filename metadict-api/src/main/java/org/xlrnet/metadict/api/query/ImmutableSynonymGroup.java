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
 * Immutable implementation of {@link SynonymGroup}.
 */
public class ImmutableSynonymGroup implements SynonymGroup, Serializable {

    private static final long serialVersionUID = -8407657557425807431L;

    private final DictionaryObject baseMeaning;

    private final Collection<DictionaryObject> synonyms;

    ImmutableSynonymGroup(DictionaryObject baseMeaning, Collection<DictionaryObject> synonyms) {
        this.baseMeaning = baseMeaning;
        this.synonyms = synonyms;
    }

    /**
     * Return a new builder instance for creating new {@link SynonymGroup} objects.
     *
     * @return a new builder.
     */
    public static SynonymGroupBuilder builder() {
        return new SynonymGroupBuilder();
    }

    /**
     * Returns the base meaning that all objects in this synonym group have.
     *
     * @return the base meaning that all objects in this synonym group have.
     */
    @Override
    public DictionaryObject getBaseMeaning() {
        return baseMeaning;
    }

    /**
     * Returns a collection of objects that are synonyms for the base meaning of this group.
     *
     * @return a collection of objects that are synonyms for the base meaning of this group.
     */
    @Override
    public Collection<DictionaryObject> getSynonyms() {
        return synonyms;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("baseMeaning", baseMeaning)
                .add("synonyms", synonyms)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableSynonymGroup)) return false;
        ImmutableSynonymGroup that = (ImmutableSynonymGroup) o;
        return Objects.equal(baseMeaning, that.baseMeaning) &&
                Objects.equal(synonyms, that.synonyms);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(baseMeaning, synonyms);
    }
}
