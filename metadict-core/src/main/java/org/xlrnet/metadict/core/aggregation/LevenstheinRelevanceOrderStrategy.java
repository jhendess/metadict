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

package org.xlrnet.metadict.core.aggregation;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.core.query.QueryRequest;

import java.util.Collection;
import java.util.Collections;

/**
 * Sort the result groups with a relevance score based on the Levensthein distance.
 */
public class LevenstheinRelevanceOrderStrategy implements OrderStrategy {

    /**
     * Sort the entries in the given result groups with the internal strategy and return a collection of {@link
     * ResultGroup} objects in the specified order. The original result set may be altered, although it is not necessary.
     *
     * @param unorderedResultGroups
     *         An unsorted collection of result groups.
     * @return a sorted collection of groups.
     */
    @NotNull
    @Override
    public Collection<ResultGroup> sortResultGroups(@NotNull QueryRequest queryRequest, @NotNull Collection<ResultGroup> unorderedResultGroups) {
        String queryString = queryRequest.getQueryString();
        for(ResultGroup group : unorderedResultGroups) {
            group.forEach(entry -> ((ResultEntryImpl) entry).setEntryScore(calculateEntryScore(entry, queryString)));
            Collections.sort(group.getResultEntries());
        }
        return unorderedResultGroups;
    }

    double calculateEntryScore(@NotNull ResultEntry entry, @NotNull String queryString) {
        int levenstheinInput = StringUtils.getLevenshteinDistance(entry.getSource().getGeneralForm().toLowerCase(), queryString.toLowerCase());
        int levenstheinOutput = Integer.MAX_VALUE;
        if (entry.getTarget() != null && entry.getTarget().getGeneralForm() != null)
            levenstheinOutput = StringUtils.getLevenshteinDistance(entry.getTarget().getGeneralForm().toLowerCase(), queryString.toLowerCase());
        int levensthein = Integer.min(levenstheinInput, levenstheinOutput);
        return 1.0 - ((double) levensthein / (1 + (double) levensthein));
    }
}
