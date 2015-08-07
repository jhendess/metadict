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

import java.util.List;
import java.util.Vector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for creating new {@link BilingualEntry} objects.
 */
public class BilingualQueryResultBuilder extends EngineQueryResultBuilder {

    private List<BilingualEntry> bilingualEntries = new Vector<>();

    BilingualQueryResultBuilder() {

    }

    /**
     * Add a new {@link BilingualEntry} to the builder. This should be used for all bilingual results of the query that
     * match the requests.
     *
     * @param bilingualEntry
     *         The {@link BilingualEntry} object - not null.
     * @return this instance of the {@link BilingualQueryResultBuilder}.
     */
    @NotNull
    public BilingualQueryResultBuilder addBilingualEntry(@NotNull BilingualEntry bilingualEntry) {
        checkNotNull(bilingualEntry);

        this.bilingualEntries.add(bilingualEntry);
        return this;
    }

    /**
     * Build a new instance of {@link BilingualQueryResult} with the previously added entries.
     *
     * @return a new instance of {@link BilingualQueryResult}.
     */
    @NotNull
    @Override
    public BilingualQueryResult build() {
        return new ImmutableBilingualQueryResult(bilingualEntries, similarRecommendations, externalContents, synonyms);
    }

}
