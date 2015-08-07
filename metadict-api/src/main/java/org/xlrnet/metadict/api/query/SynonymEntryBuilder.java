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

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for creating new {@link SynonymEntry} objects.
 */
public class SynonymEntryBuilder {

    private DictionaryObject baseObject;

    private Collection<SynonymGroup> synonymGroups = new ArrayList<>();

    SynonymEntryBuilder() {

    }

    @NotNull
    public SynonymEntry build() {
        checkNotNull(baseObject, "Base object may not be null");

        return new ImmutableSynonymEntry(baseObject, synonymGroups);
    }

    /**
     * Set the base object for which synonyms are stored in this entry. This is usually the string that was
     * originally requested.
     *
     * @return this builder
     */
    @NotNull
    public SynonymEntryBuilder setBaseObject(DictionaryObject baseObject) {
        this.baseObject = baseObject;
        return this;
    }

    /**
     * Add a new synonym group to this entry. Each different meaning of the base word should have its own synonym
     * group.
     *
     * @return this builder.
     */
    @NotNull
    public SynonymEntryBuilder addSynonymGroup(SynonymGroup synonymGroup) {
        this.synonymGroups.add(synonymGroup);
        return this;
    }


}
