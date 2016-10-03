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

import com.google.common.base.MoreObjects;
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
public class ImmutableAutoTestCase implements AutoTestCase {

    private final Optional<Language> monolingualTargetLanguage;

    private final Optional<MonolingualQueryResult> expectedMonolingualQueryResult;

    private final Optional<BilingualQueryResult> expectedBilingualResults;

    private final Optional<BilingualDictionary> bilingualTargetDictionary;

    private final String testQueryString;

    ImmutableAutoTestCase(Language monolingualTargetLanguage, MonolingualQueryResult expectedMonolingualQueryResult, BilingualQueryResult expectedBilingualResults, BilingualDictionary bilingualTargetDictionary, String testQueryString) {
        checkNotNull(testQueryString, "Test query may not be null");

        this.monolingualTargetLanguage = Optional.ofNullable(monolingualTargetLanguage);
        this.expectedMonolingualQueryResult = Optional.ofNullable(expectedMonolingualQueryResult);
        this.expectedBilingualResults = Optional.ofNullable(expectedBilingualResults);
        this.bilingualTargetDictionary = Optional.ofNullable(bilingualTargetDictionary);

        this.testQueryString = testQueryString;
    }

    public static AutoTestCaseBuilder builder() {
        return new AutoTestCaseBuilder();
    }

    @NotNull
    @Override
    public Optional<BilingualQueryResult> getExpectedBilingualResults() {
        return expectedBilingualResults;
    }

    @NotNull
    @Override
    public Optional<MonolingualQueryResult> getExpectedMonolingualResults() {
        return expectedMonolingualQueryResult;
    }

    @NotNull
    @Override
    public Optional<Language> getMonolingualTargetLanguage() {
        return monolingualTargetLanguage;
    }

    @NotNull
    @Override
    public Optional<BilingualDictionary> getBilingualTargetDictionary() {
        return bilingualTargetDictionary;
    }

    @NotNull
    @Override
    public String getTestQueryString() {
        return testQueryString;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("testQueryString", testQueryString)
                .add("monolingualTargetLanguage", monolingualTargetLanguage)
                .add("expectedMonolingualQueryResult", expectedMonolingualQueryResult)
                .add("expectedBilingualResults", expectedBilingualResults)
                .add("bilingualTargetDictionary", bilingualTargetDictionary)
                .toString();
    }
}
