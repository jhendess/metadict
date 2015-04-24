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

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ExternalContent;

import java.util.ArrayList;
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
}
