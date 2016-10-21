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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.engine.*;
import org.xlrnet.metadict.api.language.*;
import org.xlrnet.metadict.api.query.*;

/**
 * Provider for Heinzelnisse.info engine.
 */
public class HeinzelnisseEngineProvider extends AbstractSearchEngineProvider {

    @Nullable
    @Override
    public AutoTestSuite getAutoTestSuite() throws Exception {
        return ImmutableAutoTestSuite.builder()
                .addAutoTestCase(ImmutableAutoTestCase.builder()
                        .setTestQueryString("haus")
                        .setBilingualTargetDictionary(BilingualDictionary.fromQueryString("de-no", true))
                        .setExpectedBilingualResults((BilingualQueryResult) ImmutableBilingualQueryResult.builder()
                                .addBilingualEntry(ImmutableBilingualEntry.builder()
                                        .setEntryType(EntryType.NOUN)
                                        .setInputObject(ImmutableDictionaryObject.builder()
                                                .setGeneralForm("Haus")
                                                .setLanguage(Language.GERMAN)
                                                .setGrammaticalGender(GrammaticalGender.NEUTER)
                                                .setAdditionalForm(GrammaticalNumber.PLURAL, "Häuser")
                                                .build())
                                        .setOutputObject(ImmutableDictionaryObject.builder()
                                                .setGeneralForm("hus")
                                                .setGrammaticalGender(GrammaticalGender.NEUTER)
                                                .setLanguage(Language.NORWEGIAN_BOKMÅL)
                                                .build())
                                        .build())
                                .addSimilarRecommendation(ImmutableDictionaryObject.builder()
                                        .setLanguage(Language.GERMAN)
                                        .setGeneralForm("aus")
                                        .build())
                                .build())
                        .build())
                .addAutoTestCase(ImmutableAutoTestCase.builder()
                        .setTestQueryString("essen")
                        .setBilingualTargetDictionary(BilingualDictionary.fromQueryString("de-no", true))
                        .setExpectedBilingualResults((BilingualQueryResult) ImmutableBilingualQueryResult.builder()
                                .addBilingualEntry(ImmutableBilingualEntry.builder()
                                        .setEntryType(EntryType.VERB)
                                        .setInputObject(ImmutableDictionaryObject.builder()
                                                .setGeneralForm("essen")
                                                .setLanguage(Language.GERMAN)
                                                .build())
                                        .setOutputObject(ImmutableDictionaryObject.builder()
                                                .setGeneralForm("spise")
                                                .setLanguage(Language.NORWEGIAN_BOKMÅL)
                                                .setAdditionalForm(GrammaticalTense.PRESENT_TENSE, "spiser")
                                                .setAdditionalForm(GrammaticalTense.PAST_TENSE, "spiste")
                                                .setAdditionalForm(GrammaticalTense.PERFECT_PARTICIPLE, "spist")
                                                .build())
                                        .build())
                                .addSimilarRecommendation(ImmutableDictionaryObject.builder()
                                        .setLanguage(Language.GERMAN)
                                        .setGeneralForm("Esse")
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @NotNull
    @Override
    public EngineDescription getEngineDescription() {
        return ImmutableEngineDescription.builder()
                .setEngineName("Heinzelnisse")
                .setAuthorName("xolor")
                .setSearchBackendName("Heinzelnisse.info")
                .setSearchBackendLink("http://heinzelnisse.info")
                .build();
    }

    @NotNull
    @Override
    public FeatureSet getFeatureSet() {
        return ImmutableFeatureSet.builder()
                .setProvidesAlternatives(true)
                .setSupportsAutoTest(true)
                .setProvidesExternalContent(true)
                .setSupportsFuzzySearch(true)
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.NORWEGIAN, Language.GERMAN, true))
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.NORWEGIAN_BOKMÅL, Language.GERMAN, true))
                .build();
    }

    @NotNull
    @Override
    public SearchEngine newEngineInstance() {
        return new HeinzelnisseEngine();
    }
}
