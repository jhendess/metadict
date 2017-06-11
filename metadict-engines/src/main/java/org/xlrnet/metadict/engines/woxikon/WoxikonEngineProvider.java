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
import org.xlrnet.metadict.api.exception.MetadictTechnicalException;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.GrammaticalGender;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.EntryType;
import org.xlrnet.metadict.api.query.ImmutableBilingualEntry;
import org.xlrnet.metadict.api.query.ImmutableBilingualQueryResult;
import org.xlrnet.metadict.api.query.ImmutableDictionaryObject;

/**
 * Provider with a search engine for searching in various dictionaries on {@see <a
 * href="http://www.woxikon.de/">woxikon.de</a>}.
 */
public class WoxikonEngineProvider extends AbstractSearchEngineProvider {

    @Nullable
    @Override
    public AutoTestSuite getAutoTestSuite() throws MetadictTechnicalException {
        return ImmutableAutoTestSuite.builder()
                .addAutoTestCase(ImmutableAutoTestCase.builder()
                        .setTestQueryString("essen")
                        .setBilingualTargetDictionary(BilingualDictionary.fromQueryString("de-en", true))
                        .setExpectedBilingualResults(
                                ImmutableBilingualQueryResult.builder()
                                        .addBilingualEntry(ImmutableBilingualEntry.builder()
                                                .setEntryType(EntryType.VERB)
                                                .setInputObject(ImmutableDictionaryObject.builder()
                                                        .setGeneralForm("essen")
                                                        .setLanguage(Language.GERMAN)
                                                        .build())
                                                .setOutputObject(ImmutableDictionaryObject.builder()
                                                        .setGeneralForm("eat")
                                                        .setLanguage(Language.ENGLISH)
                                                        .build())
                                                .build())
                                        /* The Woxikon website seems to be quite inconsistent with their results.
                                        /* These test cases are disabled until each request returns the same response. */
                                        /*.addSimilarRecommendation(ImmutableDictionaryObject.builder()
                                                .setGeneralForm("Rasen")
                                                .setLanguage(Language.GERMAN).build())
                                        .addSimilarRecommendation(ImmutableDictionaryObject.builder()
                                                .setGeneralForm("vessel")
                                                .setLanguage(Language.ENGLISH).build())*/
                                        .build())
                        .build())
                .addAutoTestCase(ImmutableAutoTestCase.builder()
                        .setTestQueryString("Tier")
                        .setBilingualTargetDictionary(BilingualDictionary.fromQueryString("de-sv", true))
                        .setExpectedBilingualResults(
                                ImmutableBilingualQueryResult.builder()
                                        .addBilingualEntry(ImmutableBilingualEntry.builder()
                                                .setEntryType(EntryType.NOUN)
                                                .setInputObject(ImmutableDictionaryObject.builder()
                                                        .setGeneralForm("Tier")
                                                        .setDescription("allgemein")
                                                        .setGrammaticalGender(GrammaticalGender.NEUTER)
                                                        .setLanguage(Language.GERMAN)
                                                        .build())
                                                .setOutputObject(ImmutableDictionaryObject.builder()
                                                        .setGeneralForm("djur")
                                                        .setDescription("allgemein")
                                                        .setGrammaticalGender(GrammaticalGender.NEUTER)
                                                        .setLanguage(Language.SWEDISH)
                                                        .build())
                                                .build())
                                        .build())
                        .build())
                /* The Woxikon website seems to be quite inconsistent with their results.
                /* These test cases are disabled until each request returns the same response. */
                /*.addAutoTestCase(ImmutableAutoTestCase.builder()
                                .setTestQueryString("bank")
                                .setBilingualTargetDictionary(BilingualDictionary.fromQueryString("de-en", true))
                                .setExpectedBilingualResults(
                                        (BilingualQueryResult) ImmutableBilingualQueryResult.builder()
                                                .addSynonymEntry(
                                                        ImmutableSynonymEntry.builder()
                                                                //.setBaseEntryType(EntryType.NOUN)
                                                                .setBaseObject(ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "Bank"))
                                                                .addSynonymGroup(ImmutableSynonymGroup.builder()
                                                                        .setBaseMeaning(ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "Fußbank"))
                                                                        .addSynonym(ImmutableDictionaryObject.builder().setLanguage(Language.GERMAN).setGeneralForm("Schemel").setDescription("Fußbank").build())
                                                                        .build())
                                                                .build()
                                                )
                                                .build())
                                .build()
                )*/
                .build();

    }

    @NotNull
    @Override
    public EngineDescription getEngineDescription() {
        return ImmutableEngineDescription.builder()
                .setEngineName("Unofficial woxikon.de Engine")
                .setAuthorName("xolor")
                .setSearchBackendLink("http://www.woxikon.de/")
                .setSearchBackendName("woxikon.de")
                .build();
    }

    @NotNull
    @Override
    public FeatureSet getFeatureSet() {
        return ImmutableFeatureSet.builder()
                .setProvidesBilingualDictionaryEntries(true)
                .setProvidesMonolingualEntries(true)
                .setProvidesAlternatives(true)
                .setProvidesExternalContent(false)
                .setSupportsAutoTest(true)
                .setSupportsFuzzySearch(true)
                .setProvidesSynonymsOnBilingualQuery(true)
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

    @NotNull
    @Override
    public SearchEngine newEngineInstance() {
        return new WoxikonEngine();
    }
}
