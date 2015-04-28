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

package org.xlrnet.metadict.engines.heinzelnisse;

import org.xlrnet.metadict.api.engine.*;
import org.xlrnet.metadict.api.language.*;
import org.xlrnet.metadict.api.metadata.EngineDescription;
import org.xlrnet.metadict.api.metadata.EngineDescriptionBuilder;
import org.xlrnet.metadict.api.metadata.FeatureSet;
import org.xlrnet.metadict.api.metadata.FeatureSetBuilder;
import org.xlrnet.metadict.api.query.BilingualEntryBuilder;
import org.xlrnet.metadict.api.query.DictionaryObjectBuilder;
import org.xlrnet.metadict.api.query.EngineQueryResultBuilder;
import org.xlrnet.metadict.api.query.EntryType;

/**
 * Provider for Heinzelnisse.info engine.
 */
public class HeinzelnisseEngineProvider implements SearchProvider {

    /**
     * Return a set of user-defined automatic test cases. See {@link AutoTestCase} for more information. To enable
     * automatic testing, the provided {@link FeatureSet} has to return <i>true</i> when calling {@link
     * FeatureSet#isSupportsSelfTest()}.
     *
     * @return a set of user-defined automatic test cases.
     */
    @Override
    public AutoTestSuite getAutoTestSuite() {
        return new AutoTestSuiteBuilder()
                .addAutoTestCase(new AutoTestCaseBuilder()
                        .setTestQueryString("haus")
                        .setTargetDictionary(BilingualDictionary.fromQueryString("de-no", true))
                        .setExpectedResults(new EngineQueryResultBuilder()
                                .addBilingualEntry(new BilingualEntryBuilder()
                                        .setEntryType(EntryType.NOUN)
                                        .setInputObject(new DictionaryObjectBuilder()
                                                .setGeneralForm("Haus")
                                                .setLanguage(Language.GERMAN)
                                                .setGrammaticalGender(GrammaticalGender.NEUTER)
                                                .setAdditionalForm(GrammaticalNumber.PLURAL, "Häuser")
                                                .build())
                                        .setOutputObject(new DictionaryObjectBuilder()
                                                .setGeneralForm("hus")
                                                .setGrammaticalGender(GrammaticalGender.NEUTER)
                                                .setLanguage(Language.NORWEGIAN_BOKMÅL)
                                                .build())
                                        .build())
                                .addSimilarRecommendation(new DictionaryObjectBuilder()
                                        .setLanguage(Language.GERMAN)
                                        .setGeneralForm("aus")
                                        .build())
                                .build())
                        .build())
                .addAutoTestCase(new AutoTestCaseBuilder()
                        .setTestQueryString("essen")
                        .setTargetDictionary(BilingualDictionary.fromQueryString("de-no", true))
                        .setExpectedResults(new EngineQueryResultBuilder()
                                .addBilingualEntry(new BilingualEntryBuilder()
                                        .setEntryType(EntryType.VERB)
                                        .setInputObject(new DictionaryObjectBuilder()
                                                .setGeneralForm("essen")
                                                .setLanguage(Language.GERMAN)
                                                .build())
                                        .setOutputObject(new DictionaryObjectBuilder()
                                                .setGeneralForm("spise")
                                                .setLanguage(Language.NORWEGIAN_BOKMÅL)
                                                .setAdditionalForm(GrammaticalTense.PRESENT_TENSE, "spiser")
                                                .setAdditionalForm(GrammaticalTense.PAST_TENSE, "spiste")
                                                .setAdditionalForm(GrammaticalTense.PERFECT_PARTICIPLE, "spist")
                                                .build())
                                        .build())
                                .addSimilarRecommendation(new DictionaryObjectBuilder()
                                        .setLanguage(Language.GERMAN)
                                        .setGeneralForm("Esse")
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
    @Override
    public EngineDescription getEngineDescription() {
        return new EngineDescriptionBuilder()
                .setEngineName("Heinzelnisse")
                .setAuthorName("xolor")
                .setSearchBackendName("Heinzelnisse.info")
                .setSearchBackendLink("http://heinzelnisse.info")
                .build();
    }

    /**
     * Return a {@link FeatureSet} object that contains information about which features the underlying engine
     * supports. This includes e.g. the supported languages of the engine.
     *
     * @return an object that contains information about which features the underlying engine supports.
     */
    @Override
    public FeatureSet getFeatureSet() {
        return new FeatureSetBuilder()
                .setProvidesAlternatives(true)
                .setSupportsAutoTest(true)
                .setProvidesExternalContent(true)
                .setSupportsFuzzySearch(true)
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.NORWEGIAN, Language.GERMAN, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.NORWEGIAN_BOKMÅL, Language.GERMAN, true))
                .build();
    }

    /**
     * Create a new instance of the search engine. The returned engine should not be stateful and has to be implemented
     * thread-safe.
     *
     * @return a new instance of the search engine.
     */
    @Override
    public SearchEngine newEngineInstance() {
        return new HeinzelnisseEngine();
    }
}
