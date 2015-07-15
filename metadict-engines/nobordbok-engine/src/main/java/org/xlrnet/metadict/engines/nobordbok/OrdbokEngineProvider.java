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

package org.xlrnet.metadict.engines.nobordbok;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.engine.AutoTestSuite;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.engine.SearchProvider;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.metadata.EngineDescription;
import org.xlrnet.metadict.api.metadata.EngineDescriptionBuilder;
import org.xlrnet.metadict.api.metadata.FeatureSet;
import org.xlrnet.metadict.api.metadata.FeatureSetBuilder;

/**
 * Provider for Bokmålordbok and Nynorskordbok search engine.
 */
public class OrdbokEngineProvider implements SearchProvider {

    @Nullable
    @Override
    public AutoTestSuite getAutoTestSuite() {
        return null;
    }

    @NotNull
    @Override
    public EngineDescription getEngineDescription() {
        return new EngineDescriptionBuilder()
                .setEngineName("Norwegian dictionary (Bokmåls-/Nynorskordboka)")
                .setAuthorName("xolor")
                .setSearchBackendName("Bokmålsordboka | Nynorskordboka")
                .setSearchBackendLink("http://www.nob-ordbok.uio.no/")
                .build();
    }

    @NotNull
    @Override
    public FeatureSet getFeatureSet() {
        return new FeatureSetBuilder()
                .setSupportsAutoTest(false)
                .setSupportsFuzzySearch(false)
                .setProvidesAlternatives(false)
                .setProvidesExternalContent(false)
                .setProvidesMonolingualEntries(true)
                .setProvidesBilingualDictionaryEntries(false)
                .addSupportedLexicographicLanguage(Language.NORWEGIAN)
                .addSupportedLexicographicLanguage(Language.NORWEGIAN_BOKMÅL)
                .addSupportedLexicographicLanguage(Language.NORWEGIAN_NYNORSK)
                .build();
    }

    @NotNull
    @Override
    public SearchEngine newEngineInstance() {
        return new OrdbokEngine();
    }
}
