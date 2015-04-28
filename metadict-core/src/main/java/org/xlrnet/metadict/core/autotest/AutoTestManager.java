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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.engine.AutoTestCase;
import org.xlrnet.metadict.api.engine.AutoTestSuite;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.EngineQueryResult;
import org.xlrnet.metadict.api.query.ExternalContent;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Core component for running and registering {@link org.xlrnet.metadict.api.engine.AutoTestSuite} objects.
 */
@ApplicationScoped
public class AutoTestManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoTestManager.class);

    final Map<SearchEngine, AutoTestSuite> engineAutoTestSuiteMap = new HashMap<>();

    /**
     * Register the given {@link AutoTestSuite} as a test suite for the given {@link SearchEngine}. This will validate
     * the test suite and install the tests internally.
     *
     * @param searchEngine
     *         The search engine to which the test suite should be registered.
     * @param autoTestSuite
     *         The test suite that should be registered
     * @throws IllegalArgumentException
     *         Will be thrown if the given test suite contains illegal values.
     */
    public void registerAutoTestSuite(@NotNull SearchEngine searchEngine, @NotNull AutoTestSuite autoTestSuite) throws IllegalArgumentException {
        checkArgument(!engineAutoTestSuiteMap.containsKey(searchEngine), "Engine is already registered for auto testing");

        for (AutoTestCase testCase : autoTestSuite) {
            validateTestCase(testCase);
        }

        engineAutoTestSuiteMap.put(searchEngine, autoTestSuite);
    }

    /**
     * Execute all internally registered auto test cases return a full {@link AutoTestReport}.
     *
     * @return an {@link AutoTestReport} for all executed test cases.
     */
    public AutoTestReport runAllRegisteredAutoTests() {
        AutoTestReportBuilder reportBuilder = new AutoTestReportBuilder();
        engineAutoTestSuiteMap.keySet().forEach(searchEngine -> runAutoTestsForEngine(searchEngine, reportBuilder));
        return reportBuilder.build();
    }

    /**
     * Run the registered test cases for a given engine. This will create a new {@link AutoTestReportBuilder} and
     * return it after completion.
     *
     * @param searchEngine
     *         The search engine to test.
     * @return an {@link AutoTestReportBuilder} that can be used another test case.
     */
    @NotNull
    public AutoTestReportBuilder runAutoTestsForEngine(@NotNull SearchEngine searchEngine) {
        return runAutoTestsForEngine(searchEngine, new AutoTestReportBuilder());
    }

    /**
     * Run the registered test cases for a given engine. The results will be written into the provided {@link
     * AutoTestReportBuilder} object.
     *
     * @param searchEngine
     *         The search engine to test.
     * @return the given {@link AutoTestReportBuilder} filled with the results of the run tests.
     */
    @NotNull
    public AutoTestReportBuilder runAutoTestsForEngine(@NotNull SearchEngine searchEngine, @NotNull AutoTestReportBuilder reportBuilder) {
        if (!engineAutoTestSuiteMap.containsKey(searchEngine)) {
            LOGGER.warn("No registered auto tests found for engine {}", searchEngine.getClass().getCanonicalName());
            return reportBuilder;
        }

        AutoTestSuite engineTestSuite = engineAutoTestSuiteMap.get(searchEngine);

        int testCount = 1;

        LOGGER.info("Starting auto tests for engine {} ...", searchEngine.getClass().getCanonicalName());
        for (@NotNull AutoTestCase autoTestCase : engineTestSuite) {
            LOGGER.info("Running test {} for engine {}...", testCount, searchEngine.getClass().getCanonicalName());
            AutoTestResult autoTestResult = internalRunAutoTestCase(searchEngine, autoTestCase);
            reportBuilder.addAutoTestResult(autoTestResult);
            testCount++;
        }

        return reportBuilder;
    }

    @NotNull
    AutoTestResult internalRunAutoTestCase(@NotNull SearchEngine searchEngine, @NotNull AutoTestCase autoTestCase) {
        String canonicalEngineName = searchEngine.getClass().getCanonicalName();
        EngineQueryResult expectedResults = autoTestCase.getExpectedResults();
        BilingualDictionary targetDictionary = autoTestCase.getTargetDictionary();
        String requestString = autoTestCase.getTestQueryString();
        long startTime = System.currentTimeMillis();

        ResultData autoTestResult;

        try {
            autoTestResult = invokeAndValidate(searchEngine, targetDictionary, requestString, expectedResults);
            if (autoTestResult.thrownException != null) {
                LOGGER.error("Test case with query \"{}\" in dictionary {} failed", requestString, targetDictionary);
                return AutoTestResult.failed(canonicalEngineName, System.currentTimeMillis() - startTime, autoTestCase, autoTestResult.thrownException, autoTestResult.queryResult);
            }
        } catch (Exception e) {
            LOGGER.error("Internal error on test case with query \"{}\" in dictionary {}", requestString, targetDictionary, e);
            return AutoTestResult.failed(canonicalEngineName, System.currentTimeMillis() - startTime, autoTestCase, e, null);
        }

        return AutoTestResult.succeeded(canonicalEngineName, System.currentTimeMillis() - startTime, autoTestCase, autoTestResult.queryResult);
    }

    @NotNull
    ResultData invokeAndValidate(@NotNull SearchEngine searchEngine, @NotNull BilingualDictionary targetDictionary, @NotNull String requestString, @NotNull EngineQueryResult expectedResult) throws Exception {
        EngineQueryResult queryResult;
        try {
            queryResult = searchEngine.executeBilingualQuery(requestString, targetDictionary.getInput(), targetDictionary.getOutput(), targetDictionary.isBidirectional());
        } catch (Exception e) {
            return new ResultData(null, e);
        }

        List<BilingualEntry> expectedResultEntries = expectedResult.getBilingualEntries();
        List<ExternalContent> expectedExternalContents = expectedResult.getExternalContents();
        List<DictionaryObject> expectedSimilarRecommendations = expectedResult.getSimilarRecommendations();

        List<BilingualEntry> actualResultEntries = queryResult.getBilingualEntries();
        List<ExternalContent> actualExternalContents = queryResult.getExternalContents();
        List<DictionaryObject> actualSimilarRecommendations = queryResult.getSimilarRecommendations();

        try {
            validateActualObjects(expectedResultEntries, actualResultEntries);
            validateActualObjects(expectedExternalContents, actualExternalContents);
            validateActualObjects(expectedSimilarRecommendations, actualSimilarRecommendations);
        } catch (AutoTestAssertionException ae) {
            return new ResultData(queryResult, ae);
        }

        return new ResultData(queryResult, null);
    }

    /**
     * Check if the given collection of actual result objects contains all specified expected objects. If any expected
     * object is not inside the actual collection, a {@link AutoTestAssertionException} will be thrown. The validation
     * was successful if no exception was thrown.
     *
     * @param expectedObjects
     *         Collection of expected objects.
     * @param actualObjects
     *         Collection of actual objects.
     * @throws AutoTestAssertionException
     *         Will be thrown if a certain expected object could not be found in the collection of actual objects.
     */
    void validateActualObjects(Collection<?> expectedObjects, Collection<?> actualObjects) throws AutoTestAssertionException {
        for (Object expectedEntry : expectedObjects) {
            if (!actualObjects.contains(expectedEntry))
                throw new AutoTestAssertionException(expectedEntry);
        }
    }

    void validateTestCase(@NotNull AutoTestCase testCase) {
        checkNotNull(testCase.getExpectedResults(), "Expected result set may not be null");
        checkNotNull(testCase.getTargetDictionary(), "Target dictionary may not be null");
        checkNotNull(testCase.getTestQueryString(), "Test query string may not be null");
    }


    private class ResultData {

        private final EngineQueryResult queryResult;

        private final Exception         thrownException;

        public ResultData(EngineQueryResult queryResult, Exception thrownException) {
            this.queryResult = queryResult;
            this.thrownException = thrownException;
        }
    }

}
