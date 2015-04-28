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

package org.xlrnet.metadict.engines.dummy;

import org.xlrnet.metadict.api.engine.*;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.metadata.EngineDescription;
import org.xlrnet.metadict.api.metadata.EngineDescriptionBuilder;
import org.xlrnet.metadict.api.metadata.FeatureSet;
import org.xlrnet.metadict.api.metadata.FeatureSetBuilder;

/**
 * A dummy engine provider implementation without any real function.
 */
public class DummyEngineProvider implements SearchProvider {

    /**
     * Return a set of user-defined automatic test cases. See {@link AutoTestCase} for more information. To enable
     * automatic testing, the provided {@link FeatureSet} has to return <i>true</i> when calling {@link
     * FeatureSet#isSupportsSelfTest()}.
     *
     * @return a set of user-defined automatic test cases.
     */
    public AutoTestSuite getAutoTestSuite() {
        return new AutoTestSuiteBuilder().addAutoTestCase(
                new AutoTestCaseBuilder()
                        .setTestQueryString("foobar")
                        .setTargetDictionary(BilingualDictionary.fromLanguages(Language.ENGLISH, Language.GERMAN, true))
                        .build()
        ).build();
    }

    /**
     * Return an object that contains descriptive i.e. textual information about the underlying engine. This can be
     * e.g.
     * the name, url, etc. of the engine.
     */
    public EngineDescription getEngineDescription() {
        return new EngineDescriptionBuilder()
                .setEngineName("DummyEngine")
                .setSearchBackendName("DummyBackend")
                .build();
    }

    /**
     * Return an object that contains information about which features the underlying engine supports. This includes
     * e.g. the supported languages of the engine.
     */
    public FeatureSet getFeatureSet() {
        return new FeatureSetBuilder()
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.ENGLISH, Language.GERMAN, true))
                .setSupportsAutoTest(true)
                .build();
    }

    public SearchEngine newEngineInstance() {
        return new DummyEngine();
    }
}
