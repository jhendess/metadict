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

package org.xlrnet.metadict.impl.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.engine.SearchProvider;
import org.xlrnet.metadict.api.language.Dictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.metadata.EngineDescription;
import org.xlrnet.metadict.api.metadata.FeatureSet;
import org.xlrnet.metadict.api.metadata.FeatureSetBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.when;

/**
 * Test logic for {@link EngineRegistry} without CDI.
 */
public class EngineRegistryTest {

    private EngineRegistry engineRegistry;

    private SearchProvider searchProviderMock;

    @Before
    public void setUp() throws Exception {
        engineRegistry = new EngineRegistry();
        searchProviderMock = Mockito.mock(SearchProvider.class);
    }

    @Test(expected = Exception.class)
    public void testRegisterSearchProvider_fail_null_all() throws Exception {
        engineRegistry.registerSearchProvider(searchProviderMock);
    }

    @Test(expected = Exception.class)
    public void testRegisterSearchProvider_fail_null_engineDescription() throws Exception {
        when(searchProviderMock.getFeatureSet()).thenReturn(Mockito.mock(FeatureSet.class, RETURNS_SMART_NULLS));
        when(searchProviderMock.newEngineInstance()).thenReturn(Mockito.mock(SearchEngine.class, RETURNS_SMART_NULLS));

        engineRegistry.registerSearchProvider(searchProviderMock);
    }

    @Test(expected = Exception.class)
    public void testRegisterSearchProvider_fail_null_engineInstance() throws Exception {
        when(searchProviderMock.getFeatureSet()).thenReturn(Mockito.mock(FeatureSet.class, RETURNS_SMART_NULLS));
        when(searchProviderMock.getEngineDescription()).thenReturn(Mockito.mock(EngineDescription.class, RETURNS_SMART_NULLS));

        engineRegistry.registerSearchProvider(searchProviderMock);
    }

    @Test(expected = Exception.class)
    public void testRegisterSearchProvider_fail_null_featureSet() throws Exception {
        when(searchProviderMock.getEngineDescription()).thenReturn(Mockito.mock(EngineDescription.class, RETURNS_SMART_NULLS));
        when(searchProviderMock.newEngineInstance()).thenReturn(Mockito.mock(SearchEngine.class, RETURNS_SMART_NULLS));

        engineRegistry.registerSearchProvider(searchProviderMock);
    }

    @Test
    public void testRegisterSearchProvider_success() throws Exception {
        // Setup mock to return all non-null
        FeatureSet featureSet = new FeatureSetBuilder().addSupportedDictionary(Dictionary.fromLanguages(Language.GERMAN, Language.ENGLISH, true)).build();
        when(searchProviderMock.getEngineDescription()).thenReturn(Mockito.mock(EngineDescription.class, RETURNS_SMART_NULLS));
        when(searchProviderMock.getFeatureSet()).thenReturn(featureSet);
        when(searchProviderMock.newEngineInstance()).thenReturn(Mockito.mock(SearchEngine.class, RETURNS_SMART_NULLS));

        assertEquals(0, engineRegistry.countRegisteredEngines());
        engineRegistry.registerSearchProvider(searchProviderMock);
        assertEquals(1, engineRegistry.countRegisteredEngines());
    }
}