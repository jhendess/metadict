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

import java.io.Serializable;
import java.util.Collection;

/**
 * Immutable implementation of {@link SynonymEntry}.
 */
public class ImmutableSynonymEntry implements SynonymEntry, Serializable {

    private static final long serialVersionUID = -5679900170593355370L;

    private final DictionaryObject baseObject;

    private final Collection<SynonymGroup> synonymGroups;

    protected ImmutableSynonymEntry(DictionaryObject baseObject, Collection<SynonymGroup> synonymGroups) {
        this.baseObject = baseObject;
        this.synonymGroups = synonymGroups;
    }

    /**
     * Return a new builder instance for creating new {@link SynonymEntry} objects.
     *
     * @return a new builder.
     */
    public static SynonymEntryBuilder builder() {
        return new SynonymEntryBuilder();
    }

    /**
     * Returns the base object for which synonyms are stored in this entry. This is usually the string that was
     * originally requested.
     *
     * @return the base object for which synonyms are stored in this entry.
     */
    @Override
    public DictionaryObject getBaseObject() {
        return baseObject;
    }

    /**
     * Returns the synonym groups for this entry. Each different meaning of the base word should have its own synonym
     * group.
     *
     * @return the synonym groups for this entry.
     */
    @Override
    public Collection<SynonymGroup> getSynonymGroups() {
        return synonymGroups;
    }
}
