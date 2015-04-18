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

package org.xlrnet.metadict.engines.leo;

import org.xlrnet.metadict.api.engine.*;
import org.xlrnet.metadict.api.language.Dictionary;
import org.xlrnet.metadict.api.language.GrammaticalNumber;
import org.xlrnet.metadict.api.language.GrammaticalTense;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.metadata.EngineDescription;
import org.xlrnet.metadict.api.metadata.EngineDescriptionBuilder;
import org.xlrnet.metadict.api.metadata.FeatureSet;
import org.xlrnet.metadict.api.metadata.FeatureSetBuilder;
import org.xlrnet.metadict.api.query.DictionaryEntryBuilder;
import org.xlrnet.metadict.api.query.DictionaryObjectBuilder;
import org.xlrnet.metadict.api.query.EngineQueryResultBuilder;
import org.xlrnet.metadict.api.query.EntryType;

/**
 * Provider with a search engine for searching in various dictionaries on {@see <a href="http://leo.org/">leo.org</a>}.
 */
public class LeoEngineProvider implements SearchProvider {

    /**
     * Return a set of user-defined automatic test cases. See {@link AutoTestCase} for more information. To enable
     * automatic testing, the provided {@link FeatureSet} has to return <i>true</i> when calling {@link
     * FeatureSet#isSupportsSelfTest()}.
     *
     * @return a set of user-defined automatic test cases.
     */
    @Override
    public AutoTestSuite getAutoTestSuite() {
        Dictionary deEnDictionary = Dictionary.fromLanguages(Language.ENGLISH, Language.GERMAN, true);
        return
                new AutoTestSuiteBuilder()
                        .addAutoTestCase(new AutoTestCaseBuilder()
                                .setTestQueryString("eat")
                                .setTargetDictionary(deEnDictionary)
                                .setExpectedResults(new EngineQueryResultBuilder()
                                        .addEntry(new DictionaryEntryBuilder()
                                                .setEntryType(EntryType.NOUN)
                                                .setInputObject(new DictionaryObjectBuilder()
                                                        .setLanguage(Language.ENGLISH)
                                                        .setAbbreviation("MRE")
                                                        .setGeneralForm("meal ready to eat")
                                                        .setDomain("mil.")
                                                        .build())
                                                .setOutputObject(new DictionaryObjectBuilder()
                                                        .setLanguage(Language.GERMAN)
                                                        .setGeneralForm("Verpflegungspaket der US-Streitkräfte")
                                                        .build())
                                                .build())
                                        .addEntry(new DictionaryEntryBuilder()
                                                .setEntryType(EntryType.VERB)
                                                .setInputObject(new DictionaryObjectBuilder()
                                                        .setLanguage(Language.ENGLISH)
                                                        .setGeneralForm("to eat (sth.)")
                                                        .setAdditionalForm(GrammaticalTense.PAST_TENSE, "ate")
                                                        .setAdditionalForm(GrammaticalTense.PAST_PERFECT, "eaten")
                                                        .build())
                                                .setOutputObject(new DictionaryObjectBuilder()
                                                        .setLanguage(Language.GERMAN)
                                                        .setGeneralForm("(etw.) essen")
                                                        .setAdditionalForm(GrammaticalTense.PAST_TENSE, "aß")
                                                        .setAdditionalForm(GrammaticalTense.PAST_PERFECT, "gegessen")
                                                        .build())
                                                .build())
                                        .addSimilarRecommendation(new DictionaryObjectBuilder()
                                                .setGeneralForm("lesen")
                                                .setLanguage(Language.GERMAN)
                                                .build())
                                        .build())
                                .build())
                        .addAutoTestCase(new AutoTestCaseBuilder()
                                .setTestQueryString("haus")
                                .setTargetDictionary(deEnDictionary)
                                .setExpectedResults(new EngineQueryResultBuilder()
                                        .addEntry(new DictionaryEntryBuilder()
                                                .setEntryType(EntryType.NOUN)
                                                .setInputObject(new DictionaryObjectBuilder()
                                                        .setGeneralForm("house")
                                                        .setAdditionalForm(GrammaticalNumber.PLURAL, "houses")
                                                        .setLanguage(Language.ENGLISH)
                                                        .build())
                                                .setOutputObject(new DictionaryObjectBuilder()
                                                        .setGeneralForm("das Haus")
                                                        .setAdditionalForm(GrammaticalNumber.PLURAL, "die Häuser")
                                                        .setLanguage(Language.GERMAN)
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build()
                ;    // TODO: More test cases
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
                .setEngineName("Unofficial Leo Engine")
                .setAuthorName("xolor")
                .setSearchBackendLink("http://leo.org/")
                .setSearchBackendName("leo.org")
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
                .addSupportedDictionary(Dictionary.fromLanguages(Language.GERMAN, Language.ENGLISH, true))
                .addSupportedDictionary(Dictionary.fromLanguages(Language.GERMAN, Language.FRENCH, true))
                .addSupportedDictionary(Dictionary.fromLanguages(Language.GERMAN, Language.SPANISH, true))
                .addSupportedDictionary(Dictionary.fromLanguages(Language.GERMAN, Language.ITALIAN, true))
                .addSupportedDictionary(Dictionary.fromLanguages(Language.GERMAN, Language.CHINESE, true))
                .addSupportedDictionary(Dictionary.fromLanguages(Language.GERMAN, Language.RUSSIAN, true))
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
        return new LeoEngine();
    }
}
