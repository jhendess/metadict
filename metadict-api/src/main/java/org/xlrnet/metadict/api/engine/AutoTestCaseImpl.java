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
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.BilingualQueryResult;
import org.xlrnet.metadict.api.query.MonolingualQueryResult;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable implementation for {@link AutoTestCase}.
 */
public class AutoTestCaseImpl implements AutoTestCase {

    private final Optional<Language> monolingualTargetLanguage;

    private final Optional<MonolingualQueryResult> expectedMonolingualQueryResult;

    private final Optional<BilingualQueryResult> expectedBilingualResults;

    private final Optional<BilingualDictionary> bilingualTargetDictionary;

    private final String testQueryString;

    AutoTestCaseImpl(Language monolingualTargetLanguage, MonolingualQueryResult expectedMonolingualQueryResult, BilingualQueryResult expectedBilingualResults, BilingualDictionary bilingualTargetDictionary, String testQueryString) {
        checkNotNull(testQueryString, "Test query may not be null");

        this.monolingualTargetLanguage = Optional.ofNullable(monolingualTargetLanguage);
        this.expectedMonolingualQueryResult = Optional.ofNullable(expectedMonolingualQueryResult);
        this.expectedBilingualResults = Optional.ofNullable(expectedBilingualResults);
        this.bilingualTargetDictionary = Optional.ofNullable(bilingualTargetDictionary);

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
    @NotNull
    @Override
    public Optional<BilingualQueryResult> getExpectedBilingualResults() {
        return expectedBilingualResults;
    }

    /**
     * Return the expected monolingual query results for this test case. The core will only test if all of the elements
     * inside the returned object are contained inside the actual query result from {@link
     * SearchEngine#executeMonolingualQuery(String, Language)}.
     * The test will fail, if not all elements inside this expected result object can be found by value-based equality
     * inside the actual result.  If there are more elements in the actual result, the test won't fail.
     * <p>
     * Note that the monolingual test will only be executed if also a monolingual target language is also set.
     *
     * @return the expected query results for this test case.
     */
    @NotNull
    @Override
    public Optional<MonolingualQueryResult> getExpectedMonolingualResults() {
        return expectedMonolingualQueryResult;
    }

    /**
     * Return the monolingual target language which should be queried for this test case.
     * <p>
     * Note that the monolingual test will only be executed if an expected monolingual query result is also set.
     *
     * @return the monolingual target language which should be queried for this test case.
     */
    @NotNull
    @Override
    public Optional<Language> getMonolingualTargetLanguage() {
        return monolingualTargetLanguage;
    }

    /**
     * Return the target dictionary which should be queried for this test case.
     *
     * @return the target dictionary which should be queried for this test case.
     */
    @NotNull
    @Override
    public Optional<BilingualDictionary> getBilingualTargetDictionary() {
        return bilingualTargetDictionary;
    }

    /**
     * Return the query string that should be given to the engine for this test case.
     *
     * @return the query string that should be given to the engine for this test case.
     */
    @NotNull
    @Override
    public String getTestQueryString() {
        return testQueryString;
    }
}
