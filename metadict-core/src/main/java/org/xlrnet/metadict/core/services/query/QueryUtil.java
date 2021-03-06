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

package org.xlrnet.metadict.core.services.query;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.api.query.QueryStepResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Helper class with static utility methods for query-related tasks.
 */
public class QueryUtil {

    /**
     * Collect similar recommendations from multiple {@link QueryStepResult} and merge them in one list.
     *
     * @param queryStepResults
     *         The source from which the recommendations should be collected.
     * @return A list of all similar recommendations.
     */
    @NotNull
    public static List<DictionaryObject> collectSimilarRecommendations(@NotNull Iterable<QueryStepResult> queryStepResults) {
        List<DictionaryObject> similarRecommendations = new ArrayList<>();
        for (QueryStepResult queryStepResult : queryStepResults) {
            similarRecommendations.addAll(queryStepResult.getEngineQueryResult().getSimilarRecommendations());
        }
        return similarRecommendations;
    }

    /**
     * Collect all provided external content from multiple {@link QueryStepResult} and merge them in one list.
     *
     * @param queryStepResults
     *         The source from which the recommendations should be collected.
     * @return A list of all {@link ExternalContent} objects from the given object.
     */
    @NotNull
    public static List<ExternalContent> collectExternalContent(@NotNull Iterable<QueryStepResult> queryStepResults) {
        List<ExternalContent> externalContents = new ArrayList<>();
        for (QueryStepResult queryStepResult : queryStepResults) {
            externalContents.addAll(queryStepResult.getEngineQueryResult().getExternalContents());
        }
        return externalContents;
    }

    /**
     * Collect all provided synonym entries from multiple {@link QueryStepResult} and merge them in one list.
     *
     * @param queryStepResults
     *         The source from which the recommendations should be collected.
     * @return A list of all {@link ExternalContent} objects from the given object.
     */
    @NotNull
    public static List<SynonymEntry> collectSynonymEntries(@NotNull Iterable<QueryStepResult> queryStepResults) {
        List<SynonymEntry> synonymEntries = new ArrayList<>();
        for (QueryStepResult queryStepResult : queryStepResults) {
            synonymEntries.addAll(queryStepResult.getEngineQueryResult().getSynonymEntries());
        }
        return synonymEntries;
    }

    /**
     * Collect all provided monolingual entries from multiple {@link QueryStepResult} and merge them in one list. The
     * corresponding {@link org.xlrnet.metadict.api.query.MonolingualEntry} objects will only be added, if any of the
     * given {@link QueryStepResult} objects contains a {@link org.xlrnet.metadict.api.query.MonolingualQueryResult}.
     * <p>
     * As a temporary solution, the collected entries will be automatically sorted by their Levensthein distance to the
     * query string of the first {@link QueryStepResult}.
     *
     * @param queryStepResults
     *         The source from which the monolingual entries should be collected.
     * @return A list of all {@link org.xlrnet.metadict.api.query.MonolingualEntry} objects in the given {@link
     * QueryStepResult} objects.
     */
    @NotNull
    public static List<MonolingualEntry> collectMonolingualEntries(@NotNull Iterable<QueryStepResult> queryStepResults) {
        // TODO: Refactor this helper methods to not include ordering
        List<MonolingualEntry> monolingualEntries = new ArrayList<>();
        for (QueryStepResult queryStepResult : queryStepResults) {
            EngineQueryResult engineQueryResult = queryStepResult.getEngineQueryResult();
            if (!(engineQueryResult instanceof MonolingualQueryResult)) {
                continue;
            }
            monolingualEntries.addAll(((MonolingualQueryResult) engineQueryResult).getMonolingualEntries());
        }

        if (monolingualEntries.size() > 1) {
            String queryString = queryStepResults.iterator().next().getQueryStep().getQueryString();
            Comparator<MonolingualEntry> comparator = new MonolingualLevenstheinComparator(queryString);
            monolingualEntries.sort(comparator);
        }

        return monolingualEntries;
    }

    /**
     * Inner class for comparing {@link MonolingualEntry} objects according to their Levensthein distance from a given
     * query string.
     * <p>
     * Note: This class is stateful since it depends on a query string upon construction and should only be used as a
     * temporary solution!
     */
    private static class MonolingualLevenstheinComparator implements Comparator<MonolingualEntry> {

        final String queryString;

        private MonolingualLevenstheinComparator(String queryString) {
            this.queryString = queryString.toLowerCase();
        }

        @Override
        public int compare(MonolingualEntry o1, MonolingualEntry o2) {
            int l1 = -1 * StringUtils.getLevenshteinDistance(o1.getContent().getGeneralForm().toLowerCase(), this.queryString);
            int l2 = -1 * StringUtils.getLevenshteinDistance(o2.getContent().getGeneralForm().toLowerCase(), this.queryString);

            return Integer.compare(l1, l2);
        }
    }

}
