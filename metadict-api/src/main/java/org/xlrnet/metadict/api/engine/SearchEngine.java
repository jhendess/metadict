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
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.EngineQueryResult;
import org.xlrnet.metadict.api.query.EngineQueryResultBuilder;

/**
 * The interface {@link SearchEngine} represents the main part that has to be implemented to search in a backend. Each
 * search engine has to be provided by a {@link SearchProvider} implementation. The metadict core instantiates the
 * {@link SearchEngine} through the {@link SearchProvider} and queries the engine whenever it is needed.
 * <p>
 * The SearchEngine itself should be implemented as stateless as possible and always threadsafe, since the core may
 * query an engine concurrently. Caching mechanisms should <u>not</u> be implemented by the engine itself.
 * For better modularity it is also recommended that each {@link SearchEngine} searches only in one backend at the same
 * time.
 * <p>
 * However, it is allowed and encouraged to provide searches in multiple dictionaries via the same {@link SearchEngine}
 * object. The core will try to split and parallelize queries whenever possible. For more information, see {@link
 * #executeBilingualQuery(String, Language, Language, boolean)}.
 */
public interface SearchEngine {

    /**
     * The main method for querying a {@link SearchEngine}. This method will be called by the metadict core on incoming
     * search queries. The core will always try to parallelize the query as much as possible according to the specified
     * supported dictionaries of this engine.
     * <p>
     * Upon calling, the core will make sure that the language parameters of this method correspond exactly to a
     * supported {@link BilingualDictionary} as described in the engine's {@link
     * org.xlrnet.metadict.api.metadata.FeatureSet}. However, an engine may also return results from a different
     * language. In this case, the core component will decide it the supplied results are useful.
     * <p>
     * Example:
     * If the engine says it supports a one-way german-english dictionary, this method will be called with the language
     * parameters inputLanguage=GERMAN, outputLanguage=ENGLISH and allowBothWay=false.
     * However, it the engine supports a bidirectional german-english dictionary, this method will be called with the
     * language parameters inputLanguage=GERMAN, outputLanguage=ENGLISH and allowBothWay=true.
     *
     * @param queryInput
     *         The query string i.e. word that should be looked up.
     * @param inputLanguage
     *         The input language of the query. This language must be specified as a dictionary's input language of
     *         this engine.
     * @param outputLanguage
     *         The expected output language of the query. This language must be specified as the output language of the
     *         same dictionary to which the given inputLanguage belongs.
     * @param allowBothWay
     *         True, if the engine may search in both directions. I.e. the queryInput can also be seen as the
     *         outputLanguage. The core will set this flag only if the engine declared a dictionary with matching input
     *         and output language. Otherwise the will be called for each direction separately.
     * @return The results from the search query. You can use an instance of {@link EngineQueryResultBuilder}
     * to build this result list.
     */
    EngineQueryResult executeBilingualQuery(String queryInput, Language inputLanguage, Language outputLanguage, boolean allowBothWay) throws Exception;


}
