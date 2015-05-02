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

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.query.BilingualQueryResult;

/**
 * Builder for creating new {@link AutoTestCase} objects.
 */
public class AutoTestCaseBuilder {

    private BilingualQueryResult expectedBilingualResults = null;

    private BilingualDictionary targetDictionary = null;

    private String testQueryString = null;

    /**
     * Create a new instance of {@link AutoTestCase}.
     *
     * @return a new instance of {@link AutoTestCase}.
     */
    @NotNull
    public AutoTestCase build() {
        return new AutoTestCaseImpl(expectedBilingualResults, targetDictionary, testQueryString);
    }

    /**
     * Set the expected bilingual query results for this test case. The core will only test if all of the elements
     * inside the returned object are contained inside the actual query result.
     * The test will fail, if not all elements inside this expected result object can be found by value-based equality
     * inside the actual result.  If there are more elements in the actual result, the test won't fail.
     *
     * @param expectedBilingualResults
     *         the expected query results for this test case.
     * @return this builder instance.
     */
    @NotNull
    public AutoTestCaseBuilder setExpectedBilingualResults(@NotNull BilingualQueryResult expectedBilingualResults) {
        this.expectedBilingualResults = expectedBilingualResults;
        return this;
    }

    /**
     * Set the target dictionary which should be queried for this test case.
     *
     * @param targetDictionary
     *         the target dictionary which should be queried for this test case.
     * @return this builder instance.
     */
    @NotNull
    public AutoTestCaseBuilder setTargetDictionary(@NotNull BilingualDictionary targetDictionary) {
        this.targetDictionary = targetDictionary;
        return this;
    }

    /**
     * Set the query string that should be given to the engine for this test case.
     *
     * @param testQueryString
     *         the query string that should be given to the engine for this test case.
     * @return this builder instance.
     */
    @NotNull
    public AutoTestCaseBuilder setTestQueryString(@NotNull String testQueryString) {
        this.testQueryString = testQueryString;
        return this;
    }

}
