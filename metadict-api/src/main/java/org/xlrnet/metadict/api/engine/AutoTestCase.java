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

/**
 * The interface {@link AutoTestCase} provides a convenient way to model user-defined test cases. A test case may be
 * used to test if the called search backend is still working as expected.
 * <p>
 * The core may query the {@link SearchEngine} that is provided by the {@link SearchProvider} where this test case is
 * attached. Upon doing so, the core will call {@link SearchEngine#executeBilingualQuery(String, Language, Language,
 * boolean)} with derived parameters from {@link #getTestQueryString()} and {@link #getTargetDictionary()}. The
 * returned {@link BilingualQueryResult} from this query will then be compared to the returned values from {@link
 * #getExpectedBilingualResults()}.
 */
public interface AutoTestCase {

    /**
     * Return the expected bilingual query results for this test case. The core will only test if all of the elements
     * inside the returned object are contained inside the actual query result.
     * The test will fail, if not all elements inside this expected result object can be found by value-based equality
     * inside the actual result.  If there are more elements in the actual result, the test won't fail.
     *
     * @return the expected query results for this test case.
     */
    @NotNull
    BilingualQueryResult getExpectedBilingualResults();

    /**
     * Return the target dictionary which should be queried for this test case.
     *
     * @return the target dictionary which should be queried for this test case.
     */
    @NotNull
    BilingualDictionary getTargetDictionary();

    /**
     * Return the query string that should be given to the engine for this test case.
     *
     * @return the query string that should be given to the engine for this test case.
     */
    @NotNull
    String getTestQueryString();

}
