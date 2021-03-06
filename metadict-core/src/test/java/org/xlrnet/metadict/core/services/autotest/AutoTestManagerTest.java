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

package org.xlrnet.metadict.core.services.autotest;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.xlrnet.metadict.api.engine.AutoTestCase;
import org.xlrnet.metadict.api.engine.AutoTestSuite;
import org.xlrnet.metadict.api.engine.ImmutableAutoTestSuite;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

/**
 * Test cases for {@link AutoTestService} without CDI.
 */
@RunWith(MockitoJUnitRunner.class)
public class AutoTestManagerTest {

    private static final String TEST_REQUEST_STRING = "SOME_TEST_REQUEST";

    private static final String TEST_BASE_MEANING = "BASE_MEANING";

    AutoTestService autoTestServiceSpy;

    @Mock
    SearchEngine mockedSearchEngine;

    @Mock
    BilingualQueryResult expectedResult;

    @Mock
    BilingualQueryResult actualResult;

    @Before
    public void setup() {
        this.autoTestServiceSpy = new AutoTestService();
        this.autoTestServiceSpy = Mockito.spy(this.autoTestServiceSpy);
    }

    @Test
    public void testInternalRunAutoTestCase() throws Exception {

    }

    @Test
    public void testInvokeAndValidate() throws Exception {
        // Prepare mocks
        BilingualDictionary dictionary = BilingualDictionary.fromLanguages(Language.ENGLISH, Language.GERMAN, true);
        when(this.mockedSearchEngine.executeBilingualQuery(any(), any(), any(), anyBoolean())).thenReturn(this.actualResult);
        ArrayList<BilingualEntry> expectedEntriesList = new ArrayList<>();
        when(this.expectedResult.getBilingualEntries()).thenReturn(expectedEntriesList);
        ArrayList<ExternalContent> expectedExternalContentsList = new ArrayList<>();
        when(this.expectedResult.getExternalContents()).thenReturn(expectedExternalContentsList);
        ArrayList<DictionaryObject> expectedRecommendationsList = new ArrayList<>();
        when(this.expectedResult.getSimilarRecommendations()).thenReturn(expectedRecommendationsList);
        ArrayList<BilingualEntry> actualEntriesList = new ArrayList<>();
        when(this.actualResult.getBilingualEntries()).thenReturn(actualEntriesList);
        ArrayList<ExternalContent> actualExternalContentsList = new ArrayList<>();
        when(this.actualResult.getExternalContents()).thenReturn(actualExternalContentsList);
        ArrayList<DictionaryObject> actualRecommendationsList = new ArrayList<>();
        when(this.actualResult.getSimilarRecommendations()).thenReturn(actualRecommendationsList);

        // Prepare argument captor
        ArgumentCaptor<Collection> expectedValueCaptor = ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<Collection> actualValueCaptor = ArgumentCaptor.forClass(Collection.class);

        // Execute call
        this.autoTestServiceSpy.invokeBilingualAndValidate(this.mockedSearchEngine, dictionary, TEST_REQUEST_STRING, this.expectedResult);

        // Verify correct call on search engine:
        verify(this.mockedSearchEngine).executeBilingualQuery(TEST_REQUEST_STRING, Language.ENGLISH, Language.GERMAN, true);

        // Capture arguments and then assert the correct invocations:
        verify(this.autoTestServiceSpy, times(3)).validateActualObjects(expectedValueCaptor.capture(), actualValueCaptor.capture());

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
        AutoTestSuite mockedTestSuite = ImmutableAutoTestSuite.builder()
                .addAutoTestCase(mock(AutoTestCase.class))
                .addAutoTestCase(mock(AutoTestCase.class))
                .build();

        doNothing().when(this.autoTestServiceSpy).validateTestCase(any());

        this.autoTestServiceSpy.registerAutoTestSuite(this.mockedSearchEngine, mockedTestSuite);

        verify(this.autoTestServiceSpy, times(2)).validateTestCase(anyObject());
        assertEquals(1, this.autoTestServiceSpy.engineAutoTestSuiteMap.size());
        assertSame("Internal map does not contain expected test suite", mockedTestSuite, this.autoTestServiceSpy.engineAutoTestSuiteMap.get(this.mockedSearchEngine));
    }

    @Test
    @Ignore(value = "Needs to be adopted for the extended auto test structure with monolingual support")
    public void testRunAutoTestsForEngine() throws Exception {
        // Prepare mocks
        Exception mockedException = new Exception();
        AutoTestCase mockedTestCase = mock(AutoTestCase.class);
        AutoTestSuite mockedTestSuite = ImmutableAutoTestSuite.builder().addAutoTestCase(mockedTestCase).build();
        AutoTestResult testResult = AutoTestResult.failed("canonicalName", 1, mockedTestCase, mockedException, null);
        this.autoTestServiceSpy.engineAutoTestSuiteMap.put(this.mockedSearchEngine, mockedTestSuite);
        doReturn(testResult)
                .when(this.autoTestServiceSpy)
                .internalRunBilingualAutoTestCase(this.mockedSearchEngine, mockedTestCase);

        // Run call
        AutoTestReportBuilder reportBuilder = this.autoTestServiceSpy.runAutoTestsForEngine(this.mockedSearchEngine);

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

        this.autoTestServiceSpy.validateActualObjects(expectedList, actualList);

        // This test was successful if no exceptions were thrown
    }

    @Test
    public void testValidateActualObjects_containsNone() throws Exception {
        String expectedElement = "1";
        List expectedList = ImmutableList.builder().add(expectedElement).add("2").build();
        List actualList = ImmutableList.builder().add("3").add("4").build();

        try {
            this.autoTestServiceSpy.validateActualObjects(expectedList, actualList);
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
            this.autoTestServiceSpy.validateActualObjects(expectedList, actualList);
        } catch (AutoTestAssertionException e) {
            assertEquals("Thrown exception does not contain the expected causing object", missingElement, e.getExpectedObject());
        }
    }

    @Test
    public void testValidateSynonymEntries_contains() throws Exception {
        Collection<SynonymEntry> expectedSynonymEntry = ImmutableList.of(
                ImmutableSynonymEntry.builder()
                        .setBaseObject(ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, TEST_REQUEST_STRING))
                        .addSynonymGroup(ImmutableSynonymGroup.builder()
                                .setBaseMeaning(ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, TEST_BASE_MEANING))
                                .addSynonym(ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, TEST_BASE_MEANING))
                                .build())
                        .build());

        Collection<SynonymEntry> actualSynonymEntry = ImmutableList.of(
                ImmutableSynonymEntry.builder()
                        .setBaseObject(ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, TEST_REQUEST_STRING))
                        .addSynonymGroup(ImmutableSynonymGroup.builder()
                                .setBaseMeaning(ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, TEST_BASE_MEANING))
                                .addSynonym(ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, TEST_BASE_MEANING))
                                .build())
                        .build());

        this.autoTestServiceSpy.validateSynonymEntries(expectedSynonymEntry, actualSynonymEntry);
    }
}