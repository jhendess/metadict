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

package org.xlrnet.metadict.engines.woxikon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.engine.*;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.GrammaticalGender;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.*;

/**
 * Provider with a search engine for searching in various dictionaries on {@see <a
 * href="http://www.woxikon.de/">woxikon.de</a>}.
 */
public class WoxikonEngineProvider implements SearchEngineProvider {

    /**
     * Return a set of user-defined automatic test cases. See {@link AutoTestCase} for more information. To enable
     * automatic testing, the provided {@link FeatureSet} has to return <i>true</i> when calling {@link
     * FeatureSet#isSupportsSelfTest()}.
     *
     * @return a set of user-defined automatic test cases.
     */
    @Nullable
    @Override
    public AutoTestSuite getAutoTestSuite() throws Exception {
        return new AutoTestSuiteBuilder()
                .addAutoTestCase(new AutoTestCaseBuilder()
                        .setTestQueryString("essen")
                        .setBilingualTargetDictionary(BilingualDictionary.fromQueryString("de-en", true))
                        .setExpectedBilingualResults(
                                (BilingualQueryResult) new BilingualQueryResultBuilder()
                                        .addBilingualEntry(new BilingualEntryBuilder()
                                                .setEntryType(EntryType.VERB)
                                                .setInputObject(new DictionaryObjectBuilder()
                                                        .setGeneralForm("essen")
                                                        .setDescription("bestimmt")
                                                        .setLanguage(Language.GERMAN)
                                                        .build())
                                                .setOutputObject(new DictionaryObjectBuilder()
                                                        .setGeneralForm("to eat")
                                                        .setDescription("bestimmt")
                                                        .setLanguage(Language.ENGLISH)
                                                        .build())
                                                .build())
                                        .addSimilarRecommendation(new DictionaryObjectBuilder()
                                                .setGeneralForm("Rasen")
                                                .setLanguage(Language.GERMAN).build())
                                        .addSimilarRecommendation(new DictionaryObjectBuilder()
                                                .setGeneralForm("vessel")
                                                .setLanguage(Language.ENGLISH).build())
                                        .build())
                        .build())
                .addAutoTestCase(new AutoTestCaseBuilder()
                        .setTestQueryString("Tier")
                        .setBilingualTargetDictionary(BilingualDictionary.fromQueryString("de-sv", true))
                        .setExpectedBilingualResults(
                                new BilingualQueryResultBuilder()
                                        .addBilingualEntry(new BilingualEntryBuilder()
                                                .setEntryType(EntryType.NOUN)
                                                .setInputObject(new DictionaryObjectBuilder()
                                                        .setGeneralForm("reinrassiges Tier")
                                                        .setDescription("Tiere")
                                                        .setLanguage(Language.GERMAN)
                                                        .build())
                                                .setOutputObject(new DictionaryObjectBuilder()
                                                        .setGeneralForm("rasdjur")
                                                        .setDescription("Tiere")
                                                        .setGrammaticalGender(GrammaticalGender.NEUTER)
                                                        .setLanguage(Language.SWEDISH)
                                                        .build())
                                                .build())
                                        .build())
                        .build())
                .build();
    }

    /**
     * Return an {@link EngineDescription} object that contains descriptive i.e. textual information about the
     * underlying engine. This can be e.g.the name, url, etc. of the engine.
     *
     * @return an object that contains descriptive i.e. textual information about the underlying engine.
     */
    @NotNull
    @Override
    public EngineDescription getEngineDescription() {
        return new EngineDescriptionBuilder()
                .setEngineName("Unofficial woxikon.de Engine")
                .setAuthorName("xolor")
                .setSearchBackendLink("http://www.woxikon.de/")
                .setSearchBackendName("woxikon.de")
                .build();
    }

    /**
     * Return a {@link FeatureSet} object that contains information about which features the underlying engine supports.
     * This includes e.g. the supported languages of the engine.
     *
     * @return an object that contains information about which features the underlying engine supports.
     */
    @NotNull
    @Override
    public FeatureSet getFeatureSet() {
        return new FeatureSetBuilder()
                .setProvidesBilingualDictionaryEntries(true)
                .setProvidesMonolingualEntries(true)
                .setProvidesAlternatives(true)
                .setProvidesExternalContent(false)
                .setSupportsAutoTest(true)
                .setSupportsFuzzySearch(true)
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.FRENCH, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.SPANISH, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.SWEDISH, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.RUSSIAN, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.FINNISH, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.TURKISH, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.ENGLISH, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.ITALIAN, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.DUTCH, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.PORTUGUESE, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.POLISH, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.NORWEGIAN, true))
                .build();
    }

    /**
     * Create a new instance of the search engine. The returned engine should not be stateful and has to be implemented
     * thread-safe.
     *
     * @return a new instance of the search engine.
     */
    @NotNull
    @Override
    public SearchEngine newEngineInstance() {
        return new WoxikonEngine();
    }
}
