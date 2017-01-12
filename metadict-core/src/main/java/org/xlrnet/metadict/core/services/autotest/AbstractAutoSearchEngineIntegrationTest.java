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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xlrnet.metadict.api.engine.AutoTestCase;
import org.xlrnet.metadict.api.engine.AutoTestSuite;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.engine.SearchEngineProvider;
import org.xlrnet.metadict.api.exception.MetadictTechnicalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for executing the {@link AutoTestSuite} of a custom {@link SearchEngineProvider}.
 * <p>
 * You can use this class to run integration tests against external backends with this class. To register and execute
 * all tests of your own {@link SearchEngineProvider} create a new test class named like <i>MySearchEngineIT</i> where
 * <i>MySearchEngine</i> is the name of the search engine and <i>IT</i> designates the test case as an integration test.
 * Naming the test class like <i>*IT</i> makes sure that it is recognized as an integration test and runs only when
 * invoking the Maven build with the <code>verify</code> goal.
 * <p>
 * To write your own test suite, inherit from {@link AbstractAutoSearchEngineIntegrationTest} and implement a static
 * method that returns an {@link Iterable} of {@link Object} arrays. This method must then be annotated with JUnit's
 * {@link Parameterized.Parameters} annotation and return the value of {@link
 * #prepareProvider(SearchEngineProvider)}. Give a new instance of {@link SearchEngineProvider} as the parameter of this
 * method and your test case should be runnable.
 * <p>
 * Example of how to implement a custom test:
 * <pre>
 * public class MyEngineIT extends AutoSearchEngineIntegrationTest {
 *      &#64;Parameterized.Parameters
 *      public static Iterable&lt;Object[]&gt; data() throws Exception {
 *          return prepareProvider(new WoxikonEngineProvider());
 *      }
 * }
 * </pre>
 */
@RunWith(Parameterized.class)
public abstract class AbstractAutoSearchEngineIntegrationTest {

    @Parameterized.Parameter(value = 0)
    public SearchEngine searchEngine;

    @Parameterized.Parameter(value = 1)
    public AutoTestCase testCase;

    private AutoTestService autoTestService;

    public static Iterable<Object[]> prepareProvider(SearchEngineProvider searchEngineProvider) throws MetadictTechnicalException {
        List<Object[]> testCases = new ArrayList<>();

        // Initialize internal dictionary configuration:
        searchEngineProvider.getFeatureSet();

        // Test for any exceptions when calling getDescription()
        searchEngineProvider.getEngineDescription();

        // Transform auto tests into set of arrays
        AutoTestSuite autoTestSuite;
        autoTestSuite = searchEngineProvider.getAutoTestSuite();
        if (autoTestSuite != null) {
            autoTestSuite.forEach(autoTestCase -> testCases.add(new Object[]{searchEngineProvider.newEngineInstance(), autoTestCase}));
        }
        return testCases;
    }

    @Before
    public void setUp() {
        this.autoTestService = new AutoTestService();
    }

    @Test
    public void runAutoTest() throws AutoTestException {
        internalRunAutoTestCase();
    }

    void internalRunAutoTestCase() throws AutoTestException {
        AutoTestReport autoTestResults = this.autoTestService.runExternalAutoTestCase(this.searchEngine, this.testCase);

        for (AutoTestResult autoTestResult : autoTestResults.getTestResults()) {
            if (!autoTestResult.isSuccessful()) {
                System.err.println("Failure during test case execution: " + this.testCase);
                if (autoTestResult.getActualEngineQueryResult().isPresent())
                    System.err.println("Actual result: " + autoTestResult.getActualEngineQueryResult().get());

                if (autoTestResult.getThrownException().get() instanceof AutoTestAssertionException) {
                    AutoTestAssertionException testAssertionException = (AutoTestAssertionException) autoTestResult.getThrownException().get();
                    System.err.println("Expected object:     " + testAssertionException.getExpectedObject());
                    System.err.println("Most similar object: " + testAssertionException.getMostSimilarObject());
                }

                throw new AutoTestException(autoTestResult.getThrownException().get());
            }
        }
    }
}