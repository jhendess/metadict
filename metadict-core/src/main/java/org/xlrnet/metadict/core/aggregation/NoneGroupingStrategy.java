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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.BilingualQueryResult;
import org.xlrnet.metadict.core.query.QueryStepResult;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The most simple grouping strategy that merges the results of all queries in one group.
 */
public class NoneGroupingStrategy implements GroupingStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryTypeGroupingStrategy.class);

    /**
     * Group the given step results with the internal strategy and return a collection of {@link ResultGroup} objects.
     *
     * @param queryStepResults
     *         An iterable of the query steps results.
     * @return a collection of groups.
     */
    @NotNull
    @Override
    public Collection<ResultGroup> groupResultSets(@NotNull Iterable<QueryStepResult> queryStepResults) {
        ResultGroupBuilder groupBuilder = new ResultGroupBuilder().setGroupIdentifier("All results");
        boolean hasAnyObject = false;

        for (QueryStepResult stepResult : queryStepResults) {
            if (!(stepResult.getEngineQueryResult() instanceof BilingualQueryResult)) {
                LOGGER.debug("Skipping query result of type {}", stepResult.getEngineQueryResult().getClass().getCanonicalName());
                continue;
            }

            BilingualQueryResult engineQueryResult = (BilingualQueryResult) stepResult.getEngineQueryResult();
            for (BilingualEntry dictionaryEntry : engineQueryResult.getBilingualEntries()) {
                hasAnyObject = true;
                groupBuilder.addResultEntry(ResultEntryImpl.from(dictionaryEntry, stepResult.getQueryStep().getSearchEngineName(), 1.0));
            }
        }

        Collection<ResultGroup> resultGroups = new ArrayList<>();
        if (hasAnyObject)
            resultGroups.add(groupBuilder.build());
        return resultGroups;
    }
}
