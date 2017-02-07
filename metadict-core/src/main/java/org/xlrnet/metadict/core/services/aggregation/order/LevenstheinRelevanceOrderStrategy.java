/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
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

package org.xlrnet.metadict.core.services.aggregation.order;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.core.api.aggregation.Group;
import org.xlrnet.metadict.core.api.aggregation.OrderStrategy;
import org.xlrnet.metadict.core.api.aggregation.ResultEntry;
import org.xlrnet.metadict.core.api.query.QueryRequest;
import org.xlrnet.metadict.core.services.aggregation.group.GroupBuilder;
import org.xlrnet.metadict.core.services.aggregation.group.ScoredResultEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sort the result groups with a relevance score based on the Levensthein distance.
 */
public class LevenstheinRelevanceOrderStrategy implements OrderStrategy {

    @NotNull
    @Override
    public Collection<Group<ResultEntry>> sortResultGroups(@NotNull QueryRequest queryRequest, @NotNull Collection<Group<BilingualEntry>> unorderedResultGroups) {
        String queryString = queryRequest.getQueryString();
        Collection<Group<ResultEntry>> sortedGroups = new ArrayList<>(unorderedResultGroups.size());
        for (Group<BilingualEntry> group : unorderedResultGroups) {
            sortedGroups.add(sortGroup(group, queryString));
        }
        return sortedGroups;
    }

    private Group<ResultEntry> sortGroup(@NotNull Group<BilingualEntry> group, @NotNull String queryString) {
        List<ResultEntry> sortedEntries = group.getResultEntries()
                .stream()
                .map(entry -> ScoredResultEntry.from(entry, calculateEntryScore(entry, queryString)))
                .sorted()
                .collect(Collectors.toList());

        return new GroupBuilder<ResultEntry>().addAll(sortedEntries).setGroupIdentifier(group.getGroupIdentifier()).build();
    }

    double calculateEntryScore(@NotNull BilingualEntry entry, @NotNull String queryString) {
        int levenstheinInput = StringUtils.getLevenshteinDistance(entry.getSource().getGeneralForm().toLowerCase(), queryString.toLowerCase());
        int levenstheinOutput = Integer.MAX_VALUE;
        if (entry.getTarget() != null && entry.getTarget().getGeneralForm() != null) {
            levenstheinOutput = StringUtils.getLevenshteinDistance(entry.getTarget().getGeneralForm().toLowerCase(), queryString.toLowerCase());
        }
        int levensthein = Integer.min(levenstheinInput, levenstheinOutput);
        return 1.0 - ((double) levensthein / (1 + (double) levensthein));
    }
}
