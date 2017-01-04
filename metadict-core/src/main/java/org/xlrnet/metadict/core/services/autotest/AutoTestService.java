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

import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.engine.AutoTestCase;
import org.xlrnet.metadict.api.engine.AutoTestSuite;
import org.xlrnet.metadict.api.engine.ImmutableAutoTestSuite;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.util.SimilarityUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Core component for running and registering {@link org.xlrnet.metadict.api.engine.AutoTestSuite} objects.
 */
@Singleton
public class AutoTestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoTestService.class);

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
    public void registerAutoTestSuite(@NotNull SearchEngine searchEngine, @NotNull AutoTestSuite autoTestSuite) {
        checkArgument(!this.engineAutoTestSuiteMap.containsKey(searchEngine), "Engine is already registered for auto testing");

        for (AutoTestCase testCase : autoTestSuite) {
            validateTestCase(testCase);
        }

        this.engineAutoTestSuiteMap.put(searchEngine, autoTestSuite);
    }

    /**
     * Execute all internally registered auto test cases return a full {@link AutoTestReport}.
     *
     * @return an {@link AutoTestReport} for all executed test cases.
     */
    public AutoTestReport runAllRegisteredAutoTests() {
        AutoTestReportBuilder reportBuilder = new AutoTestReportBuilder();
        this.engineAutoTestSuiteMap.keySet().forEach(searchEngine -> runAutoTestsForEngine(searchEngine, reportBuilder));
        return reportBuilder.build();
    }

    /**
     * Run the registered test cases for a given engine. This will create a new {@link AutoTestReportBuilder} and
     * return
     * it after completion.
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
        if (!this.engineAutoTestSuiteMap.containsKey(searchEngine)) {
            LOGGER.warn("No registered auto tests found for engine {}", searchEngine.getClass().getCanonicalName());
            return reportBuilder;
        }

        AutoTestSuite engineTestSuite = this.engineAutoTestSuiteMap.get(searchEngine);
        internalRunAutoTestSuite(searchEngine, reportBuilder, engineTestSuite);

        return reportBuilder;
    }

    /**
     * Run a specific {@link AutoTestCase} for an external {@link SearchEngine}. By calling this method, you can
     * explicitly execute a single auto test case without having to start a full Metadict instance. This may be handy
     * when you want to execute unit tests on your engine. The result will be a complete {@link AutoTestReport}. Note,
     * that you auto test case may contain both a monolingual and bilingual test case.
     *
     * @param searchEngine
     *         The engine to test. The supplied auto test case will be called with this engine.
     * @param autoTestCase
     *         The auto test case to execute.
     * @return The report with all results from this test.
     */
    public AutoTestReport runExternalAutoTestCase(@NotNull SearchEngine searchEngine, @NotNull AutoTestCase autoTestCase) {
        validateTestCase(autoTestCase);

        AutoTestSuite autoTestSuiteWrapper = ImmutableAutoTestSuite.builder().addAutoTestCase(autoTestCase).build();
        AutoTestReportBuilder reportBuilder = new AutoTestReportBuilder();

        internalRunAutoTestSuite(searchEngine, reportBuilder, autoTestSuiteWrapper);

        return reportBuilder.build();
    }

    @NotNull
    AutoTestResult internalRunBilingualAutoTestCase(@NotNull SearchEngine searchEngine, @NotNull AutoTestCase autoTestCase) {
        BilingualQueryResult expectedResults = autoTestCase.getExpectedBilingualResults().get();
        BilingualDictionary targetDictionary = autoTestCase.getBilingualTargetDictionary().get();
        String canonicalEngineName = searchEngine.getClass().getCanonicalName();
        String requestString = autoTestCase.getTestQueryString();
        long startTime = System.currentTimeMillis();

        ResultData autoTestResult;

        try {
            autoTestResult = invokeBilingualAndValidate(searchEngine, targetDictionary, requestString, expectedResults);
            if (autoTestResult.thrownException != null) {
                LOGGER.error("Bilingual test case with query \"{}\" in dictionary {} failed", requestString, targetDictionary);
                return AutoTestResult.failed(canonicalEngineName, System.currentTimeMillis() - startTime, autoTestCase, autoTestResult.thrownException, autoTestResult.queryResult);
            }
        } catch (Exception e) {
            LOGGER.error("Internal error on bilingual test case with query \"{}\" in dictionary {}", requestString, targetDictionary, e);
            return AutoTestResult.failed(canonicalEngineName, System.currentTimeMillis() - startTime, autoTestCase, e, null);
        }

        return AutoTestResult.succeeded(canonicalEngineName, System.currentTimeMillis() - startTime, autoTestCase, autoTestResult.queryResult);
    }

    @NotNull
    AutoTestResult internalRunMonolingualAutoTestCase(@NotNull SearchEngine searchEngine, @NotNull AutoTestCase autoTestCase) {
        MonolingualQueryResult expectedResults = autoTestCase.getExpectedMonolingualResults().get();
        Language targetLanguage = autoTestCase.getMonolingualTargetLanguage().get();
        String canonicalEngineName = searchEngine.getClass().getCanonicalName();
        String requestString = autoTestCase.getTestQueryString();
        long startTime = System.currentTimeMillis();

        ResultData autoTestResult;

        try {
            autoTestResult = invokeMonolingualAndValidate(searchEngine, targetLanguage, requestString, expectedResults);
            if (autoTestResult.thrownException != null) {
                LOGGER.error("Monolingual test case with query \"{}\" with and {} failed", requestString, targetLanguage);
                return AutoTestResult.failed(canonicalEngineName, System.currentTimeMillis() - startTime, autoTestCase, autoTestResult.thrownException, autoTestResult.queryResult);
            }
        } catch (Exception e) {
            LOGGER.error("Internal error on monolingual test case with query \"{}\" and language {}", requestString, targetLanguage, e);
            return AutoTestResult.failed(canonicalEngineName, System.currentTimeMillis() - startTime, autoTestCase, e, null);
        }

        return AutoTestResult.succeeded(canonicalEngineName, System.currentTimeMillis() - startTime, autoTestCase, autoTestResult.queryResult);
    }

    @NotNull
    ResultData invokeBilingualAndValidate(@NotNull SearchEngine searchEngine, @NotNull BilingualDictionary targetDictionary, @NotNull String requestString, @NotNull BilingualQueryResult expectedResult) {
        BilingualQueryResult queryResult;
        try {
            queryResult = searchEngine.executeBilingualQuery(requestString, targetDictionary.getSource(), targetDictionary.getTarget(), targetDictionary.isBidirectional());
        } catch (Exception e) {
            return new ResultData(null, e);
        }

        List<BilingualEntry> expectedResultEntries = expectedResult.getBilingualEntries();
        List<ExternalContent> expectedExternalContents = expectedResult.getExternalContents();
        List<DictionaryObject> expectedSimilarRecommendations = expectedResult.getSimilarRecommendations();
        List<SynonymEntry> expectedSynonymEntries = expectedResult.getSynonymEntries();

        List<BilingualEntry> actualResultEntries = queryResult.getBilingualEntries();
        List<ExternalContent> actualExternalContents = queryResult.getExternalContents();
        List<DictionaryObject> actualSimilarRecommendations = queryResult.getSimilarRecommendations();
        List<SynonymEntry> actualSynonymEntries = queryResult.getSynonymEntries();

        try {
            validateActualObjects(expectedResultEntries, actualResultEntries);
            validateActualObjects(expectedExternalContents, actualExternalContents);
            validateActualObjects(expectedSimilarRecommendations, actualSimilarRecommendations);
            validateSynonymEntries(expectedSynonymEntries, actualSynonymEntries);
        } catch (AutoTestAssertionException ae) {
            return new ResultData(queryResult, ae);
        }

        return new ResultData(queryResult, null);
    }

    @NotNull
    ResultData invokeMonolingualAndValidate(@NotNull SearchEngine searchEngine, @NotNull Language targetLanguage, @NotNull String requestString, @NotNull MonolingualQueryResult expectedResult) {
        MonolingualQueryResult queryResult;
        try {
            queryResult = searchEngine.executeMonolingualQuery(requestString, targetLanguage);
        } catch (Exception e) {
            return new ResultData(null, e);
        }

        List<MonolingualEntry> expectedResultEntries = expectedResult.getMonolingualEntries();
        List<ExternalContent> expectedExternalContents = expectedResult.getExternalContents();
        List<DictionaryObject> expectedSimilarRecommendations = expectedResult.getSimilarRecommendations();
        List<SynonymEntry> expectedSynonymEntries = expectedResult.getSynonymEntries();

        List<MonolingualEntry> actualResultEntries = queryResult.getMonolingualEntries();
        List<ExternalContent> actualExternalContents = queryResult.getExternalContents();
        List<DictionaryObject> actualSimilarRecommendations = queryResult.getSimilarRecommendations();
        List<SynonymEntry> actualSynonymEntries = queryResult.getSynonymEntries();

        try {
            validateActualObjects(expectedResultEntries, actualResultEntries);
            validateActualObjects(expectedExternalContents, actualExternalContents);
            validateActualObjects(expectedSimilarRecommendations, actualSimilarRecommendations);
            validateSynonymEntries(expectedSynonymEntries, actualSynonymEntries);
        } catch (AutoTestAssertionException ae) {
            return new ResultData(queryResult, ae);
        }

        return new ResultData(queryResult, null);
    }

    /**
     * Check if the given collection of actual result objects contains all specified expected objects. If any expected
     * object is not inside the actual collection, a {@link AutoTestAssertionException} will be thrown containing the
     * object that is most similar to the expected. The validation was successful if no exception was thrown.
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

            boolean containsEntry = false;
            double bestSimilarity = 0.0;
            Object mostSimilar = null;

            for (Object actualObject : actualObjects) {
                double similarity = SimilarityUtils.deepFieldSimilarity(expectedEntry, actualObject);

                if (similarity == 1.0) {        // NOSONAR
                    containsEntry = true;
                    break;
                } else if (similarity > bestSimilarity) {
                    mostSimilar = actualObject;
                }
            }

            if (!containsEntry) {
                throw new AutoTestAssertionException(expectedEntry, mostSimilar);
            }
        }
    }

    void validateSynonymEntries(Collection<SynonymEntry> expectedSynonymEntries, Collection<SynonymEntry> actualSynonymEntries) throws AutoTestAssertionException {
        if (expectedSynonymEntries.isEmpty()) {
            return;
        }

        for (SynonymEntry expectedSynonymEntry : expectedSynonymEntries) {
            double bestSimilarity = 0.0;
            boolean containsEntry = false;
            SynonymEntry mostSimilarSynonymEntry = null;
            DictionaryObject expectedBaseObject = expectedSynonymEntry.getBaseObject();

            for (SynonymEntry actualSynonymEntry : actualSynonymEntries) {
                DictionaryObject actualBaseObject = actualSynonymEntry.getBaseObject();
                double similarity = 0.75 * SimilarityUtils.deepFieldSimilarity(expectedBaseObject, actualBaseObject);
                similarity += 0.25 * SimilarityUtils.fieldSimilarity(expectedSynonymEntry.getBaseEntryType(), actualSynonymEntry.getBaseEntryType());

                if (similarity == 1.0) {        // NOSONAR
                    validateSynonymGroups(expectedSynonymEntry.getSynonymGroups(), actualSynonymEntry.getSynonymGroups());
                    containsEntry = true;
                    break;
                } else if (similarity > bestSimilarity) {
                    mostSimilarSynonymEntry = actualSynonymEntry;
                }
            }
            if (!containsEntry) {
                throw new AutoTestAssertionException(expectedSynonymEntry, mostSimilarSynonymEntry);
            }
        }

    }

    void validateSynonymGroups(Collection<SynonymGroup> expectedSynonymGroups, Collection<SynonymGroup> actualSynonymGroups) throws AutoTestAssertionException {
        if (expectedSynonymGroups.isEmpty()) {
            return;
        }

        boolean containsEntry = false;
        SynonymGroup mostSimilarSynonymGroup = null;
        for (SynonymGroup expectedSynonymEntry : expectedSynonymGroups) {
            double bestSimilarity = 0.0;
            DictionaryObject expectedBaseObject = expectedSynonymEntry.getBaseMeaning();

            for (SynonymGroup actualSynonymGroup : actualSynonymGroups) {
                double similarity = SimilarityUtils.deepFieldSimilarity(expectedBaseObject, actualSynonymGroup.getBaseMeaning());

                if (similarity == 1.0) {    // NOSONAR
                    validateActualObjects(expectedSynonymEntry.getSynonyms(), actualSynonymGroup.getSynonyms());
                    containsEntry = true;
                    break;
                } else if (similarity > bestSimilarity) {
                    mostSimilarSynonymGroup = actualSynonymGroup;
                }
            }
        }
        if (!containsEntry) {
            throw new AutoTestAssertionException(expectedSynonymGroups, mostSimilarSynonymGroup);
        }
    }

    void validateTestCase(@NotNull AutoTestCase testCase) {
        checkNotNull(testCase.getExpectedBilingualResults(), "Expected result set may not be null");
        checkNotNull(testCase.getBilingualTargetDictionary(), "Target dictionary may not be null");
        checkNotNull(testCase.getTestQueryString(), "Test query string may not be null");
    }

    private void internalRunAutoTestSuite(@NotNull SearchEngine searchEngine, @NotNull AutoTestReportBuilder reportBuilder, @NotNull AutoTestSuite engineTestSuite) {
        int testCount = 1;

        LOGGER.info("Starting auto tests for engine {} ...", searchEngine.getClass().getCanonicalName());
        for (@NotNull AutoTestCase autoTestCase : engineTestSuite) {

            if (autoTestCase.getExpectedBilingualResults().isPresent() && autoTestCase.getBilingualTargetDictionary().isPresent()) {
                LOGGER.info("Running bilingual test {} for engine {}...", testCount, searchEngine.getClass().getCanonicalName());
                AutoTestResult autoTestResult = internalRunBilingualAutoTestCase(searchEngine, autoTestCase);
                reportBuilder.addAutoTestResult(autoTestResult);
                testCount++;
            }

            if (autoTestCase.getExpectedMonolingualResults().isPresent() && autoTestCase.getMonolingualTargetLanguage().isPresent()) {
                LOGGER.info("Running monolingual test {} for engine {}...", testCount, searchEngine.getClass().getCanonicalName());
                AutoTestResult autoTestResult = internalRunMonolingualAutoTestCase(searchEngine, autoTestCase);
                reportBuilder.addAutoTestResult(autoTestResult);
                testCount++;
            }

        }
    }

    private class ResultData {

        private final EngineQueryResult queryResult;

        private final Exception thrownException;

        public ResultData(EngineQueryResult queryResult, Exception thrownException) {
            this.queryResult = queryResult;
            this.thrownException = thrownException;
        }
    }

}
