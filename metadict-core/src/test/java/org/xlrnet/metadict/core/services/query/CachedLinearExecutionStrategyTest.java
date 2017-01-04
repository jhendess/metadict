/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakob Hendeß
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.exception.MetadictTechnicalException;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.BilingualQueryResult;
import org.xlrnet.metadict.api.storage.StorageOperationException;
import org.xlrnet.metadict.core.api.query.QueryStepResult;
import org.xlrnet.metadict.core.services.storage.InMemoryStorage;

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

    private CachedLinearExecutionStrategy executionStrategy;

    @Mock
    private SearchEngine engineMock;

    @Mock
    private QueryStepResult stepResultMock;

    @Mock
    private BilingualQueryResult resultMock;

    private InMemoryStorage storageService;

    @Before
    public void setup() {
        this.storageService = Mockito.spy(new InMemoryStorage());
        this.executionStrategy = Mockito.spy(new CachedLinearExecutionStrategy(this.storageService));
    }

    @Test
    public void testExecuteQueryPlan_successful() throws Exception {
        AbstractQueryStep queryStep = getQueryStepMock();
        QueryPlan queryPlan = new QueryPlan().addQueryStep(queryStep);
        when(this.stepResultMock.isFailedStep()).thenReturn(false);

        doReturn(this.stepResultMock).when(this.executionStrategy).executeQueryStep(any());

        Collection<QueryStepResult> queryStepResults = this.executionStrategy.executeQueryPlan(queryPlan);

        verify(this.executionStrategy).executeQueryPlan(queryPlan);
        assertEquals(queryStepResults.size(), 1);
        assertEquals(this.stepResultMock, queryStepResults.iterator().next());
        assertNotNull(this.executionStrategy.queryStepResultCache.getIfPresent(queryStep));
    }

    @Test
    public void testExecuteQueryPlan_failedStep() throws Exception {
        AbstractQueryStep queryStep = getQueryStepMock();
        QueryPlan queryPlan = new QueryPlan().addQueryStep(queryStep);
        when(this.stepResultMock.isFailedStep()).thenReturn(true);

        doReturn(this.stepResultMock).when(this.executionStrategy).executeQueryStep(any());

        Collection<QueryStepResult> queryStepResults = this.executionStrategy.executeQueryPlan(queryPlan);

        verify(this.executionStrategy).executeQueryPlan(queryPlan);
        assertEquals(queryStepResults.size(), 1);
        assertEquals(this.stepResultMock, queryStepResults.iterator().next());
        assertNull(this.executionStrategy.queryStepResultCache.getIfPresent(queryStep));
    }

    @Test
    public void testExecuteQueryPlan_stepThrows() throws Exception {
        AbstractQueryStep queryStep = getQueryStepMock();
        QueryPlan queryPlan = new QueryPlan().addQueryStep(queryStep);

        doThrow(new RuntimeException("Exception")).when(this.executionStrategy).executeQueryStep(any());

        Collection<QueryStepResult> queryStepResults = this.executionStrategy.executeQueryPlan(queryPlan);

        verify(this.executionStrategy).executeQueryPlan(queryPlan);
        assertEquals(queryStepResults.size(), 1);

        QueryStepResult queryStepResult = queryStepResults.iterator().next();

        assertTrue(queryStepResult.isFailedStep());
        assertEquals("java.lang.RuntimeException: Exception", queryStepResult.getErrorMessage());
        assertNull(this.executionStrategy.queryStepResultCache.getIfPresent(queryStep));
    }

    @Test
    public void testExecuteQueryPlan_storageThrows() throws Exception {
        AbstractQueryStep queryStep = getQueryStepMock();
        QueryPlan queryPlan = new QueryPlan().addQueryStep(queryStep);
        when(this.engineMock.executeBilingualQuery(anyString(), any(Language.class), any(Language.class), anyBoolean())).thenReturn(this.resultMock);

        doThrow(new StorageOperationException("message", "ns", "key")).when(this.storageService).read(any(), any(), any());

        Collection<QueryStepResult> queryStepResults = this.executionStrategy.executeQueryPlan(queryPlan);

        assertEquals(queryStepResults.size(), 1);

        verify(this.storageService).delete(anyString(), anyString());
        verify(this.executionStrategy).executeQueryStep(queryStep);

        QueryStepResult queryStepResult = queryStepResults.iterator().next();

        assertFalse(queryStepResult.isFailedStep());
        assertNotNull(this.executionStrategy.queryStepResultCache.getIfPresent(queryStep));
    }

    @Test
    public void testExecuteQueryStep_returnsNull() throws Exception {
        AbstractQueryStep queryStep = getQueryStepMock();
        when(this.engineMock.executeBilingualQuery(anyString(), any(Language.class), any(Language.class), anyBoolean())).thenReturn(null);

        QueryStepResult queryStepResult = this.executionStrategy.executeQueryStep(queryStep);

        verify(this.engineMock).executeBilingualQuery(eq(QUERY_STRING), eq(Language.ENGLISH), eq(Language.GERMAN), eq(true));

        assertTrue(queryStepResult.isFailedStep());
        assertNotNull(queryStepResult.getErrorMessage());
        assertNotNull(queryStepResult.getEngineQueryResult());
        assertEquals(queryStep, queryStepResult.getQueryStep());
    }

    @Test
    public void testExecuteQueryStep_succesful() throws Exception {
        AbstractQueryStep queryStep = getQueryStepMock();

        when(this.engineMock.executeBilingualQuery(anyString(), any(Language.class), any(Language.class), anyBoolean())).thenReturn(this.resultMock);

        QueryStepResult queryStepResult = this.executionStrategy.executeQueryStep(queryStep);

        verify(this.engineMock).executeBilingualQuery(eq(QUERY_STRING), eq(Language.ENGLISH), eq(Language.GERMAN), eq(true));

        assertFalse(queryStepResult.isFailedStep());
        assertNull(queryStepResult.getErrorMessage());
        assertEquals(this.resultMock, queryStepResult.getEngineQueryResult());
        assertEquals(queryStep, queryStepResult.getQueryStep());
    }

    @Test
    public void testExecuteQueryStep_throwsException() throws Exception {
        AbstractQueryStep queryStep = getQueryStepMock();
        when(this.engineMock.executeBilingualQuery(anyString(), any(Language.class), any(Language.class), anyBoolean())).thenThrow(new MetadictTechnicalException("Exception"));

        QueryStepResult queryStepResult = this.executionStrategy.executeQueryStep(queryStep);

        verify(this.engineMock).executeBilingualQuery(eq(QUERY_STRING), eq(Language.ENGLISH), eq(Language.GERMAN), eq(true));

        assertTrue(queryStepResult.isFailedStep());
        assertEquals("Exception", queryStepResult.getErrorMessage());
        assertNotNull(queryStepResult.getEngineQueryResult());
        assertEquals(queryStep, queryStepResult.getQueryStep());
    }

    private AbstractQueryStep getQueryStepMock() {
        return new BilingualQueryStep()
                .setAllowBothWay(true)
                .setInputLanguage(Language.ENGLISH)
                .setOutputLanguage(Language.GERMAN)
                .setQueryString(QUERY_STRING)
                .setSearchEngine(this.engineMock)
                .setSearchEngineName("mockedEngine");
    }
}