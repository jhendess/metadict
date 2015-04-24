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

import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.Mockito;
import org.xlrnet.metadict.api.query.DictionaryEntryBuilder;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.core.query.QueryRequest;

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
        assertEquals(0.5, strategy.calculateEntryScore(resultEntry, "hase"), 0.01);
    }

    @Test
    public void testCalculateEntryScore_inverted() throws Exception {
        ResultEntry resultEntry = createResultEntry("huse", "hase");
        assertEquals(1.0, strategy.calculateEntryScore(resultEntry, "hase"), 0.01);
    }

    @Test
    public void testCalculateEntryScore_null() throws Exception {
        ResultEntry resultEntry = createResultEntry("hus", null);
        assertEquals(0.3333, strategy.calculateEntryScore(resultEntry, "hase"), 0.01);
    }

    @Test
    public void testCalculateEntryScore_uppercase() throws Exception {
        ResultEntry resultEntry = createResultEntry("hus", "HASE");
        assertEquals(1.0, strategy.calculateEntryScore(resultEntry, "hase"), 0.01);
    }

    @Test
    public void testSortResultGroups() throws Exception {

        ResultEntry resultEntry_10 = createResultEntry("huse", "hase");
        ResultEntry resultEntry_03 = createResultEntry("hus", null);
        ResultEntry resultEntry_05 = createResultEntry("huse", "hause");

        ResultGroup resultGroup = new ResultGroupBuilder()
                .addResultEntry(resultEntry_05)
                .addResultEntry(resultEntry_03)
                .addResultEntry(resultEntry_10)
                .build();

        Collection<ResultGroup> testGroups = Lists.<ResultGroup>newArrayList(resultGroup);
        QueryRequest queryRequest = createQueryRequestMock("hase");
        Collection<ResultGroup> sortedGroups = strategy.sortResultGroups(queryRequest, testGroups);

        ResultGroup group = sortedGroups.iterator().next();

        List<ResultEntry> resultEntries = group.getResultEntries();

        assertEquals(resultEntry_10, resultEntries.get(0));
        assertEquals(resultEntry_05, resultEntries.get(1));
        assertEquals(resultEntry_03, resultEntries.get(2));
    }

    private QueryRequest createQueryRequestMock(String requestString) {
        QueryRequest queryRequest = Mockito.mock(QueryRequest.class);
        when(queryRequest.getQueryString()).thenReturn(requestString);
        return queryRequest;
    }

    private ResultEntry createResultEntry(String generalFormInput, String generalFormOutput) {

        DictionaryObject inputObjectMock = Mockito.mock(DictionaryObject.class);
        when(inputObjectMock.getGeneralForm()).thenReturn(generalFormInput);

        DictionaryObject outputObjectMock = Mockito.mock(DictionaryObject.class);
        when(outputObjectMock.getGeneralForm()).thenReturn(generalFormOutput);

        return ResultEntryImpl.from(
                new DictionaryEntryBuilder()
                        .setInputObject(inputObjectMock)
                        .setOutputObject(outputObjectMock)
                        .build(),
                "", 1.0);

    }

}