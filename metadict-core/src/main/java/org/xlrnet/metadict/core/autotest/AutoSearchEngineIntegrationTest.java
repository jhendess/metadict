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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xlrnet.metadict.api.engine.AutoTestCase;
import org.xlrnet.metadict.api.engine.AutoTestSuite;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.engine.SearchProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for executing the {@link AutoTestSuite} of a custom {@link SearchProvider}.
 * <p>
 * You can use this class to run integration tests against external backends with this class. To register and execute
 * all tests of your own {@link SearchProvider} create a new test class named like <i>MySearchEngineIT</i> where
 * <i>MySearchEngine</i> is the name of the search engine and <i>IT</i> designates the test case as an integration test.
 * Naming the test class like <i>*IT</i> makes sure that it is recognized as an integration test and runs only when
 * invoking the Maven build with the <code>verify</code> goal.
 * <p>
 * To write your own test suite, inherit from {@link AutoSearchEngineIntegrationTest} and implement a static method that
 * returns an {@link Iterable} of {@link Object} arrays. This method must then be annotated with JUnit's {@link
 * org.junit.runners.Parameterized.Parameters} annotation and return the value of {@link
 * #prepareProvider(SearchProvider)}. Give a new instance of {@link SearchProvider} as the parameter of this method and
 * your test case should be runnable.
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
public abstract class AutoSearchEngineIntegrationTest {

    @Parameterized.Parameter(value = 0)
    public SearchEngine searchEngine;

    @Parameterized.Parameter(value = 1)
    public AutoTestCase testCase;

    private AutoTestManager autoTestManager;

    public static Iterable<Object[]> prepareProvider(SearchProvider searchEngineProvider) throws Exception {
        List<Object[]> testCases = new ArrayList<>();

        // Initialize internal dictionary configuration:
        searchEngineProvider.getFeatureSet();

        // Transform auto tests into set of arrays
        AutoTestSuite autoTestSuite = searchEngineProvider.getAutoTestSuite();
        if (autoTestSuite != null)
            autoTestSuite.forEach(autoTestCase -> testCases.add(new Object[]{searchEngineProvider.newEngineInstance(), autoTestCase}));

        return testCases;
    }

    @Before
    public void setUp() throws Exception {
        autoTestManager = new AutoTestManager();
    }

    @Test
    public void runAutoTest() throws Exception {
        internalRunAutoTestCase();
    }

    void internalRunAutoTestCase() throws Exception {
        AutoTestReport autoTestResults = autoTestManager.runExternalAutoTestCase(searchEngine, testCase);

        for (AutoTestResult autoTestResult : autoTestResults.getTestResults()) {
            if (!autoTestResult.isSuccessful()) {
                System.err.println("Failure during test case execution: " + testCase);
                System.err.println("Actual result: " + autoTestResult.getActualEngineQueryResult().get());
                throw autoTestResult.getThrownException().get();
            }
        }
    }
}