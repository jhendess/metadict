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

package org.xlrnet.metadict.core.strategies;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.EngineQueryResult;
import org.xlrnet.metadict.core.query.QueryPlan;
import org.xlrnet.metadict.core.query.QueryStep;
import org.xlrnet.metadict.core.query.QueryStepResult;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test cases for {@link CachedLinearExecutionStrategy}.
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class CachedLinearExecutionStrategyTest {

    private static final String QUERY_STRING = "queryString";

    @Spy
    CachedLinearExecutionStrategy executionStrategy;

    @Mock
    SearchEngine engineMock;

    @Mock
    QueryStepResult stepResultMock;

    @Test
    public void testExecuteQueryPlan_successful() throws Exception {
        QueryStep queryStep = getQueryStepMock();
        QueryPlan queryPlan = new QueryPlan().addQueryStep(queryStep);
        when(stepResultMock.isFailedStep()).thenReturn(false);

        doReturn(stepResultMock).when(executionStrategy).executeQueryStep(any());

        Collection<QueryStepResult> queryStepResults = executionStrategy.executeQueryPlan(queryPlan);

        verify(executionStrategy).executeQueryPlan(queryPlan);
        assertEquals(queryStepResults.size(), 1);
        assertEquals(stepResultMock, queryStepResults.iterator().next());
        assertNotNull(executionStrategy.queryStepResultCache.getIfPresent(queryStep));
    }

    @Test
    public void testExecuteQueryPlan_failedStep() throws Exception {
        QueryStep queryStep = getQueryStepMock();
        QueryPlan queryPlan = new QueryPlan().addQueryStep(queryStep);
        when(stepResultMock.isFailedStep()).thenReturn(true);

        doReturn(stepResultMock).when(executionStrategy).executeQueryStep(any());

        Collection<QueryStepResult> queryStepResults = executionStrategy.executeQueryPlan(queryPlan);

        verify(executionStrategy).executeQueryPlan(queryPlan);
        assertEquals(queryStepResults.size(), 1);
        assertEquals(stepResultMock, queryStepResults.iterator().next());
        assertNull(executionStrategy.queryStepResultCache.getIfPresent(queryStep));
    }

    @Test
    public void testExecuteQueryPlan_stepThrows() throws Exception {
        QueryStep queryStep = getQueryStepMock();
        QueryPlan queryPlan = new QueryPlan().addQueryStep(queryStep);

        doThrow(new RuntimeException("Exception")).when(executionStrategy).executeQueryStep(any());

        Collection<QueryStepResult> queryStepResults = executionStrategy.executeQueryPlan(queryPlan);

        verify(executionStrategy).executeQueryPlan(queryPlan);
        assertEquals(queryStepResults.size(), 1);

        QueryStepResult queryStepResult = queryStepResults.iterator().next();

        assertTrue(queryStepResult.isFailedStep());
        assertEquals("java.lang.RuntimeException: Exception", queryStepResult.getErrorMessage());
        assertNull(executionStrategy.queryStepResultCache.getIfPresent(queryStep));
    }

    @Test
    public void testExecuteQueryStep_returnsNull() throws Exception {
        QueryStep queryStep = getQueryStepMock();
        when(engineMock.executeSearchQuery(anyString(), any(Language.class), any(Language.class), anyBoolean())).thenReturn(null);

        QueryStepResult queryStepResult = executionStrategy.executeQueryStep(queryStep);

        verify(engineMock).executeSearchQuery(eq(QUERY_STRING), eq(Language.ENGLISH), eq(Language.GERMAN), eq(true));

        assertTrue(queryStepResult.isFailedStep());
        assertNotNull(queryStepResult.getErrorMessage());
        assertNotNull(queryStepResult.getEngineQueryResult());
        assertEquals(queryStep, queryStepResult.getQueryStep());
    }

    @Test
    public void testExecuteQueryStep_succesful() throws Exception {
        QueryStep queryStep = getQueryStepMock();
        EngineQueryResult resultMock = Mockito.mock(EngineQueryResult.class);

        when(engineMock.executeSearchQuery(anyString(), any(Language.class), any(Language.class), anyBoolean())).thenReturn(resultMock);

        QueryStepResult queryStepResult = executionStrategy.executeQueryStep(queryStep);

        verify(engineMock).executeSearchQuery(eq(QUERY_STRING), eq(Language.ENGLISH), eq(Language.GERMAN), eq(true));

        assertFalse(queryStepResult.isFailedStep());
        assertNull(queryStepResult.getErrorMessage());
        assertEquals(resultMock, queryStepResult.getEngineQueryResult());
        assertEquals(queryStep, queryStepResult.getQueryStep());
    }

    @Test
    public void testExecuteQueryStep_throwsException() throws Exception {
        QueryStep queryStep = getQueryStepMock();
        when(engineMock.executeSearchQuery(anyString(), any(Language.class), any(Language.class), anyBoolean())).thenThrow(new Exception("Exception"));

        QueryStepResult queryStepResult = executionStrategy.executeQueryStep(queryStep);

        verify(engineMock).executeSearchQuery(eq(QUERY_STRING), eq(Language.ENGLISH), eq(Language.GERMAN), eq(true));

        assertTrue(queryStepResult.isFailedStep());
        assertEquals("Exception", queryStepResult.getErrorMessage());
        assertNotNull(queryStepResult.getEngineQueryResult());
        assertEquals(queryStep, queryStepResult.getQueryStep());
    }

    private QueryStep getQueryStepMock() {
        return new QueryStep()
                .setQueryString(QUERY_STRING)
                .setAllowBothWay(true)
                .setInputLanguage(Language.ENGLISH)
                .setOutputLanguage(Language.GERMAN)
                .setSearchEngine(engineMock)
                .setSearchEngineName("mockedEngine");
    }
}