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

package org.xlrnet.metadict.core.services.aggregation.group;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.BilingualQueryResult;
import org.xlrnet.metadict.api.query.EntryType;
import org.xlrnet.metadict.core.api.aggregation.Group;
import org.xlrnet.metadict.core.api.aggregation.GroupingStrategy;
import org.xlrnet.metadict.core.api.query.QueryStepResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Grouping strategy that creates a group for each used entrytype and groups the entries according to it.
 */
public class EntryTypeGroupingStrategy implements GroupingStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryTypeGroupingStrategy.class);

    /**
     * Group the given step results with the internal strategy and return a collection of {@link Group} objects.
     *
     * @param queryStepResults
     *         An iterable of the query steps results.
     * @return a collection of groups.
     */
    @NotNull
    @Override
    public Collection<Group<BilingualEntry>> groupResultSets(@NotNull Iterable<QueryStepResult> queryStepResults) {
        Map<EntryType, GroupBuilder<BilingualEntry>> entryTypeGroupBuilderMap = new HashMap<>();
        Collection<Group<BilingualEntry>> resultGroupCollection = new ArrayList<>();

        for (QueryStepResult stepResult : queryStepResults) {
            if (!(stepResult.getEngineQueryResult() instanceof BilingualQueryResult)) {
                LOGGER.debug("Skipping query result of type {}", stepResult.getEngineQueryResult().getClass().getCanonicalName());
                continue;
            }

            BilingualQueryResult engineQueryResult = (BilingualQueryResult) stepResult.getEngineQueryResult();
            for (BilingualEntry entry : engineQueryResult.getBilingualEntries()) {
                EntryType entryType = entry.getEntryType();
                if (!entryTypeGroupBuilderMap.containsKey(entryType)) {
                    String groupIdentifier = (entryType != EntryType.UNKNOWN) ? entryType.getDisplayname() + "s" : "Unknown";
                    GroupBuilder<BilingualEntry> groupBuilder = new GroupBuilder<BilingualEntry>().setGroupIdentifier(groupIdentifier);
                    entryTypeGroupBuilderMap.put(entryType, groupBuilder);
                }
                entryTypeGroupBuilderMap.get(entryType).add(entry);
            }
        }

        for (GroupBuilder<BilingualEntry> groupBuilder : entryTypeGroupBuilderMap.values()) {
            resultGroupCollection.add(groupBuilder.build());
        }

        return resultGroupCollection;
    }
}
