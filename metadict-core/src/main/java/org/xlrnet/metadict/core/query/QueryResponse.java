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

package org.xlrnet.metadict.core.query;

import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.aggregation.GroupingType;
import org.xlrnet.metadict.core.aggregation.ResultEntry;
import org.xlrnet.metadict.core.aggregation.ResultGroup;

import java.util.Collection;

/**
 * The class {@link QueryResponse} represents the final results of a query. It contains both a grouped view and
 * an ungrouped view on the result set.
 */
public interface QueryResponse {

    // TODO: Add status object

    /**
     * Returns a {@link Collection} with all available {@link ExternalContent} objects from the query.
     *
     * @return all available {@link ExternalContent} objects from the query.
     */
    Collection<ExternalContent> getExternalContents();

    /**
     * Returns a view on the underlying bilingual result set based on the requested grouping mechanism. Each element of
     * {@link ResultGroup} in the returned collection contains the {@link ResultEntry} objects that were matched to
     * this
     * group.
     *
     * @return a view on the underlying result set based on the requested grouping mechanism.
     */
    Collection<ResultGroup> getGroupedBilingualResults();

    /**
     * Returns the {@link GroupingType} that was used for grouping the resulting set.
     *
     * @return the {@link GroupingType} that was used for grouping the resulting set.
     */
    GroupingType getGroupingType();

    /**
     * Returns a {@link Collection} of monolingual entries in the result sets. Monolingual entries are currently
     * <i>not</i> grouped.
     *
     * @return a {@link Collection} of monolingual entries in the result sets.
     */
    Collection<MonolingualEntry> getMonolingualEntries();

    /**
     * Returns internal performance information about the query.
     *
     * @return internal performance information about the query.
     */
    QueryPerformanceStatistics getPerformanceStatistics();

    /**
     * Returns the original input query.
     *
     * @return the original input query.
     */
    String getRequestString();

    /**
     * Returns a {@link Collection} with additional search recommendations for the user. Search recommendations are
     * only available in one language (i.e. no translation provided).
     *
     * @return a collection with additional search recommendations for the user.
     */
    Collection<DictionaryObject> getSimilarRecommendations();

    /**
     * Returns a monolingual set of synonym entries for single objects. Each entry
     * represents all synonyms for a certain object (word, phrase, etc.) where the synonyms are grouped into
     * different
     * {@link SynonymGroup} objects.
     *
     * @return a monolingual set of synonym entries for single objects.
     */
    Collection<SynonymEntry> getSynonymEntries();

    /**
     * Returns an {@link Iterable} that can be used to iterate over all {@link ResultEntry} objects of the query. Each
     * {@link ResultEntry} object that can be obtained via {@link ResultGroup} must also be accessible through the
     * returned iterator of this method.
     *
     * @return an {@link Iterable} that can be used to iterate over all {@link ResultEntry} objects of the query.
     */
    Iterable<ResultEntry> getUngroupedBilingualResults();

}
