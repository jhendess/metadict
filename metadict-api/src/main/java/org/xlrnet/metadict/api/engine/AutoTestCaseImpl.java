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

package org.xlrnet.metadict.api.engine;

import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.query.EngineQueryResult;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable implementation for {@link AutoTestCase}.
 */
public class AutoTestCaseImpl implements AutoTestCase {

    private final EngineQueryResult expectedResults;

    private final BilingualDictionary targetDictionary;

    private final String testQueryString;

    AutoTestCaseImpl(EngineQueryResult expectedResults, BilingualDictionary targetDictionary, String testQueryString) {
        checkNotNull(expectedResults, "Expected results may not be null");
        checkNotNull(targetDictionary, "Target dictionary may not be null");
        checkNotNull(testQueryString, "Test query may not be null");

        this.expectedResults = expectedResults;
        this.targetDictionary = targetDictionary;
        this.testQueryString = testQueryString;
    }

    /**
     * Return the expected query results for this test case. The core will only test if all of the elements inside the
     * returned object are contained inside the actual query result.
     * The test will fail, if not all elements inside this expected result object can be found by value-based equality
     * inside the actual result.  If there are more elements in the actual result, the test won't fail.
     *
     * @return the expected query results for this test case.
     */
    @Override
    public EngineQueryResult getExpectedResults() {
        return expectedResults;
    }

    /**
     * Return the target dictionary which should be queried for this test case.
     *
     * @return the target dictionary which should be queried for this test case.
     */
    @Override
    public BilingualDictionary getTargetDictionary() {
        return targetDictionary;
    }

    /**
     * Return the query string that should be given to the engine for this test case.
     *
     * @return the query string that should be given to the engine for this test case.
     */
    @Override
    public String getTestQueryString() {
        return testQueryString;
    }
}
