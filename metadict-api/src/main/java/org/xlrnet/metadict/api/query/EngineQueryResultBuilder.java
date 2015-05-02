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
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.engine.SearchProvider;
import org.xlrnet.metadict.api.metadata.FeatureSet;

import java.util.List;
import java.util.Vector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract builder for engine query results.
 */
public abstract class EngineQueryResultBuilder {

    List<DictionaryObject> similarRecommendations = new Vector<>();

    List<ExternalContent> externalContents = new Vector<>();

    /**
     * Add a new {@link ExternalContent} object to the builder. This is used to describe any content provided by a
     * {@link SearchEngine} that does not represent a lexicographic element. This may e.g. be
     * external links to forums or other useful resources.
     * <p>
     * When adding any objects through this method, the provided {@link org.xlrnet.metadict.api.metadata.FeatureSet}
     * from the {@link SearchProvider} should return <i>true</i> on {@link
     * FeatureSet#isProvidesExternalContent()}.
     *
     * @param externalContent
     *         The {@link ExternalContent} object - not null.
     * @return this instance of the {@link EngineQueryResultBuilder}.
     */
    @NotNull
    public EngineQueryResultBuilder addExternalContent(@NotNull ExternalContent externalContent) {
        checkNotNull(externalContent);

        this.externalContents.add(externalContent);
        return this;
    }

    /**
     * Add a new {@link DictionaryObject} as a similar word recommendation to the builder. This should be used whenever
     * the search engine can recommend an alternate term the user might be interested in.
     * <p>
     * When adding any objects through this method, the provided {@link FeatureSet}
     * from the {@link SearchProvider} should return <i>true</i> on {@link
     * FeatureSet#isProvidesAlternatives()}.
     *
     * @param dictionaryObject
     *         The {@link DictionaryObject} object - not null.
     * @return this instance of the {@link EngineQueryResultBuilder}.
     */
    @NotNull
    public EngineQueryResultBuilder addSimilarRecommendation(@NotNull DictionaryObject dictionaryObject) {
        checkNotNull(dictionaryObject);

        this.similarRecommendations.add(dictionaryObject);
        return this;
    }

    @NotNull
    public abstract EngineQueryResult build();
}
