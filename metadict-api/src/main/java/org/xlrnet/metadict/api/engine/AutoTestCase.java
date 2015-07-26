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

/**
 * The interface {@link AutoTestCase} provides a convenient way to model user-defined test cases. A test case may be
 * used to test if the called search backend is still working as expected.
 * <p>
 * Auto test cases support both monolingual and bilingual queries. If a monolingual query should be executed both
 * {@link
 * #getMonolingualTargetLanguage()} and {@link #getExpectedMonolingualResults()} may not return empty Optionals. If a
 * bilingual query should be executed {@link
 * #getBilingualTargetDictionary()} and {@link #getExpectedBilingualResults()} ()} may not return empty Optionals.
 * <p>
 * The core may query the {@link SearchEngine} that is provided by the {@link SearchEngineProvider} where this test case is
 * attached. Upon doing so, the core will call {@link SearchEngine#executeMonolingualQuery(String, Language)} if
 * monolingual test case data is available and {@link SearchEngine#executeBilingualQuery(String, Language, Language,
 * boolean)} if bilingual test case data is available. The execute-methods will be called with derived parameters from
 * {@link #getTestQueryString()} and {@link #getBilingualTargetDictionary()} or the according {@link
 * #getMonolingualTargetLanguage()}. The returned query result will then be compared to the returned values from
 * respective method with the expected results.
 */
public interface AutoTestCase {

    /**
     * Return the expected bilingual query results for this test case. The core will only test if all of the elements
     * inside the returned object are contained inside the actual query result from {@link
     * SearchEngine#executeBilingualQuery(String, Language, Language, boolean)}.
     * The test will fail, if not all elements inside this expected result object can be found by value-based equality
     * inside the actual result.  If there are more elements in the actual result, the test won't fail.
     * <p>
     * Note that the bilingual test will only be executed if a bilingual target dictionary is also set.
     *
     * @return the expected query results for this test case.
     */
    @NotNull
    Optional<BilingualQueryResult> getExpectedBilingualResults();

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
    Optional<MonolingualQueryResult> getExpectedMonolingualResults();

    /**
     * Return the monolingual target language which should be queried for this test case.
     * <p>
     * Note that the monolingual test will only be executed if an expected monolingual query result is also set.
     *
     * @return the monolingual target language which should be queried for this test case.
     */
    @NotNull
    Optional<Language> getMonolingualTargetLanguage();

    /**
     * Return the bilingual target dictionary which should be queried for this test case.
     * <p>
     * Note that the bilingual test will only be executed if an expected bilingual query result is also set.
     *
     * @return the target dictionary which should be queried for this test case.
     */
    @NotNull
    Optional<BilingualDictionary> getBilingualTargetDictionary();

    /**
     * Return the query string that should be given to the engine for this test case.
     *
     * @return the query string that should be given to the engine for this test case.
     */
    @NotNull
    String getTestQueryString();

}
