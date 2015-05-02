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

package org.xlrnet.metadict.core.autotest;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.xlrnet.metadict.api.engine.AutoTestCase;
import org.xlrnet.metadict.api.engine.AutoTestSuite;
import org.xlrnet.metadict.api.engine.AutoTestSuiteBuilder;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.BilingualQueryResult;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ExternalContent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

/**
 * Test cases for {@link AutoTestManager} without CDI.
 */
@RunWith(MockitoJUnitRunner.class)
public class AutoTestManagerTest {

    private static final String TEST_REQUEST_STRING = "SOME_TEST_REQUEST";

    AutoTestManager autoTestManagerSpy;

    @Mock
    SearchEngine mockedSearchEngine;

    @Mock
    BilingualQueryResult expectedResult;

    @Mock
    BilingualQueryResult actualResult;

    @Before
    public void setup() {
        autoTestManagerSpy = new AutoTestManager();
        autoTestManagerSpy = Mockito.spy(autoTestManagerSpy);
    }

    @Test
    public void testInternalRunAutoTestCase() throws Exception {

    }

    @Test
    public void testInvokeAndValidate() throws Exception {
        // Prepare mocks
        BilingualDictionary dictionary = BilingualDictionary.fromLanguages(Language.ENGLISH, Language.GERMAN, true);
        when(mockedSearchEngine.executeBilingualQuery(any(), any(), any(), anyBoolean())).thenReturn(actualResult);
        ArrayList<BilingualEntry> expectedEntriesList = new ArrayList<>();
        when(expectedResult.getBilingualEntries()).thenReturn(expectedEntriesList);
        ArrayList<ExternalContent> expectedExternalContentsList = new ArrayList<>();
        when(expectedResult.getExternalContents()).thenReturn(expectedExternalContentsList);
        ArrayList<DictionaryObject> expectedRecommendationsList = new ArrayList<>();
        when(expectedResult.getSimilarRecommendations()).thenReturn(expectedRecommendationsList);
        ArrayList<BilingualEntry> actualEntriesList = new ArrayList<>();
        when(actualResult.getBilingualEntries()).thenReturn(actualEntriesList);
        ArrayList<ExternalContent> actualExternalContentsList = new ArrayList<>();
        when(actualResult.getExternalContents()).thenReturn(actualExternalContentsList);
        ArrayList<DictionaryObject> actualRecommendationsList = new ArrayList<>();
        when(actualResult.getSimilarRecommendations()).thenReturn(actualRecommendationsList);

        // Prepare argument captor
        ArgumentCaptor<Collection> expectedValueCaptor = ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<Collection> actualValueCaptor = ArgumentCaptor.forClass(Collection.class);

        // Execute call
        autoTestManagerSpy.invokeAndValidate(mockedSearchEngine, dictionary, TEST_REQUEST_STRING, expectedResult);

        // Verify correct call on search engine:
        verify(mockedSearchEngine).executeBilingualQuery(TEST_REQUEST_STRING, Language.ENGLISH, Language.GERMAN, true);

        // Capture arguments and then assert the correct invocations:
        verify(autoTestManagerSpy, times(3)).validateActualObjects(expectedValueCaptor.capture(), actualValueCaptor.capture());

        List<Collection> capturedExpectedValues = expectedValueCaptor.getAllValues();
        List<Collection> capturedActualValues = actualValueCaptor.getAllValues();

        for (int i = 0; i < capturedExpectedValues.size(); i++) {
            if (capturedExpectedValues.get(i) == expectedEntriesList) {
                assertSame("Actual entries list was not correctly validated against expected entries list", actualEntriesList, capturedActualValues.get(i));
            } else if (capturedExpectedValues.get(i) == expectedExternalContentsList) {
                assertSame("Actual external content list was not correctly validated against expected external content list", actualExternalContentsList, capturedActualValues.get(i));
            } else if (capturedExpectedValues.get(i) == expectedRecommendationsList) {
                assertSame("Actual recommendations list was not correctly validated against expected recommendations list", actualRecommendationsList, capturedActualValues.get(i));
            } else {
                fail("Unexpected value in expected argument list?!");
            }
        }
    }

    @Test
    public void testRegisterAutoTestSuite() throws Exception {
        AutoTestSuite mockedTestSuite = new AutoTestSuiteBuilder()
                .addAutoTestCase(mock(AutoTestCase.class))
                .addAutoTestCase(mock(AutoTestCase.class))
                .build();

        doNothing().when(autoTestManagerSpy).validateTestCase(any());

        autoTestManagerSpy.registerAutoTestSuite(mockedSearchEngine, mockedTestSuite);

        verify(autoTestManagerSpy, times(2)).validateTestCase(anyObject());
        assertEquals(1, autoTestManagerSpy.engineAutoTestSuiteMap.size());
        assertSame("Internal map does not contain expected test suite", mockedTestSuite, autoTestManagerSpy.engineAutoTestSuiteMap.get(mockedSearchEngine));
    }

    @Test
    public void testRunAutoTestsForEngine() throws Exception {
        // Prepare mocks
        Exception mockedException = new Exception();
        AutoTestCase mockedTestCase = mock(AutoTestCase.class);
        AutoTestSuite mockedTestSuite = new AutoTestSuiteBuilder().addAutoTestCase(mockedTestCase).build();
        AutoTestResult testResult = AutoTestResult.failed("canonicalName", 1, mockedTestCase, mockedException, null);
        autoTestManagerSpy.engineAutoTestSuiteMap.put(mockedSearchEngine, mockedTestSuite);
        doReturn(testResult)
                .when(autoTestManagerSpy)
                .internalRunAutoTestCase(mockedSearchEngine, mockedTestCase);

        // Run call
        AutoTestReportBuilder reportBuilder = autoTestManagerSpy.runAutoTestsForEngine(mockedSearchEngine);

        // Assertions against builder
        assertNotNull(reportBuilder);

        // Assertions against report
        AutoTestReport report = reportBuilder.build();
        assertEquals(1, report.getFailedTests());
        assertEquals(1, report.getTotalTestCount());
        assertEquals(0, report.getSuccessfulTests());

        assertEquals(1, report.getTestResults().size());
        assertEquals(testResult, report.getTestResults().get(0));
    }

    @Test
    public void testValidateActualObjects_containsAll() throws Exception {
        ImmutableList.Builder<Object> listBuilder = ImmutableList.builder().add("1").add("2");
        List expectedList = listBuilder.build();
        List actualList = listBuilder.add("3").build();

        autoTestManagerSpy.validateActualObjects(expectedList, actualList);

        // This test was successful if no exceptions were thrown
    }

    @Test
    public void testValidateActualObjects_containsNone() throws Exception {
        String expectedElement = "1";
        List expectedList = ImmutableList.builder().add(expectedElement).add("2").build();
        List actualList = ImmutableList.builder().add("3").add("4").build();

        try {
            autoTestManagerSpy.validateActualObjects(expectedList, actualList);
        } catch (AutoTestAssertionException e) {
            assertEquals("Thrown exception does not contain the expected causing object", expectedElement, e.getExpectedObject());
        }
    }

    @Test
    public void testValidateActualObjects_containsPartially() throws Exception {
        String expectedElement = "1";
        String missingElement = "2";
        List expectedList = ImmutableList.builder().add(expectedElement).add(missingElement).build();
        List actualList = ImmutableList.builder().add("3").add(expectedElement).build();

        try {
            autoTestManagerSpy.validateActualObjects(expectedList, actualList);
        } catch (AutoTestAssertionException e) {
            assertEquals("Thrown exception does not contain the expected causing object", missingElement, e.getExpectedObject());
        }
    }
}