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
 * Builder for creating new {@link SynonymGroup} objects.
 */
public class SynonymGroupBuilder {

    private DictionaryObject baseMeaning;

    private Collection<DictionaryObject> synonyms = new ArrayList<>();

    SynonymGroupBuilder() {

    }

    @NotNull
    public SynonymGroup build() {
        checkNotNull(baseMeaning, "Base meaning may not be null");

        return new ImmutableSynonymGroup(baseMeaning, synonyms);
    }

    /**
     * Sets the base meaning that all objects in this synonym group resemble.
     *
     * @return this builder.
     */
    @NotNull
    public SynonymGroupBuilder setBaseMeaning(DictionaryObject baseMeaning) {
        this.baseMeaning = baseMeaning;
        return this;
    }

    /**
     * Add a new synonym for the base meaning of this group.
     *
     * @return this builder.
     */
    @NotNull
    public SynonymGroupBuilder addSynonym(DictionaryObject synonym) {
        this.synonyms.add(synonym);
        return this;
    }
}
