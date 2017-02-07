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

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.mockito.Mockito;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ImmutableBilingualEntry;
import org.xlrnet.metadict.core.api.aggregation.Group;
import org.xlrnet.metadict.core.api.aggregation.ResultEntry;
import org.xlrnet.metadict.core.api.query.QueryRequest;
import org.xlrnet.metadict.core.services.aggregation.group.GroupBuilder;
import org.xlrnet.metadict.core.services.aggregation.group.ScoredResultEntry;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Basic tests for the levensthein-based relevance.
 */
public class LevenstheinRelevanceOrderStrategyTest {

    LevenstheinRelevanceOrderStrategy strategy = new LevenstheinRelevanceOrderStrategy();

    @Test
    public void testCalculateEntryScore() throws Exception {
        ResultEntry resultEntry = createResultEntry("huse", "hause");
        assertEquals(0.5, this.strategy.calculateEntryScore(resultEntry, "hase"), 0.01);
    }

    @Test
    public void testCalculateEntryScore_inverted() throws Exception {
        ResultEntry resultEntry = createResultEntry("huse", "hase");
        assertEquals(1.0, this.strategy.calculateEntryScore(resultEntry, "hase"), 0.01);
    }

    @Test
    public void testCalculateEntryScore_null() throws Exception {
        ResultEntry resultEntry = createResultEntry("hus", null);
        assertEquals(0.3333, this.strategy.calculateEntryScore(resultEntry, "hase"), 0.01);
    }

    @Test
    public void testCalculateEntryScore_uppercase() throws Exception {
        ResultEntry resultEntry = createResultEntry("hus", "HASE");
        assertEquals(1.0, this.strategy.calculateEntryScore(resultEntry, "hase"), 0.01);
    }

    @Test
    public void testSortResultGroups() throws Exception {

        BilingualEntry resultEntry_10 = createBilingualEntry("huse", "hase");
        BilingualEntry resultEntry_03 = createBilingualEntry("hus", null);
        BilingualEntry resultEntry_05 = createBilingualEntry("huse", "hause");

        Group<BilingualEntry> resultGroup = new GroupBuilder<BilingualEntry>()
                .add(resultEntry_05)
                .add(resultEntry_03)
                .add(resultEntry_10)
                .setGroupIdentifier("")
                .build();

        Collection<Group<BilingualEntry>> testGroups = Lists.<Group<BilingualEntry>>newArrayList(resultGroup);
        QueryRequest queryRequest = createQueryRequestMock("hase");
        Collection<Group<ResultEntry>> sortedGroups = this.strategy.sortResultGroups(queryRequest, testGroups);

        Group<ResultEntry> group = sortedGroups.iterator().next();

        List<ResultEntry> resultEntries = group.getResultEntries();

        assertEquals(resultEntry_10, ((ScoredResultEntry) resultEntries.get(0)).unwrap());
        assertEquals(resultEntry_05, ((ScoredResultEntry) resultEntries.get(1)).unwrap());
        assertEquals(resultEntry_03, ((ScoredResultEntry) resultEntries.get(2)).unwrap());
    }

    private QueryRequest createQueryRequestMock(String requestString) {
        QueryRequest queryRequest = Mockito.mock(QueryRequest.class);
        when(queryRequest.getQueryString()).thenReturn(requestString);
        return queryRequest;
    }

    private ResultEntry createResultEntry(String generalFormInput, String generalFormOutput) {
        return ScoredResultEntry.from(
                createBilingualEntry(generalFormInput, generalFormOutput), 1.0);

    }

    @NotNull
    private BilingualEntry createBilingualEntry(String generalFormInput, String generalFormOutput) {
        DictionaryObject inputObjectMock = Mockito.mock(DictionaryObject.class);
        when(inputObjectMock.getGeneralForm()).thenReturn(generalFormInput);

        DictionaryObject outputObjectMock = Mockito.mock(DictionaryObject.class);
        when(outputObjectMock.getGeneralForm()).thenReturn(generalFormOutput);
        return ImmutableBilingualEntry.builder()
                .setInputObject(inputObjectMock)
                .setOutputObject(outputObjectMock)
                .build();
    }

}