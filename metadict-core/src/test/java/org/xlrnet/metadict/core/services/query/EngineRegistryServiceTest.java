/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakob Hendeß
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

package org.xlrnet.metadict.core.services.query;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xlrnet.metadict.api.engine.*;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.when;

/**
 * Test logic for {@link EngineRegistryService} without CDI.
 */
public class EngineRegistryServiceTest {

    private EngineRegistryService engineRegistryService;

    private SearchEngineProvider searchEngineProviderMock;

    @Before
    public void setUp() throws Exception {
        this.engineRegistryService = new EngineRegistryService();
        this.searchEngineProviderMock = Mockito.mock(SearchEngineProvider.class);
    }

    @Test(expected = Exception.class)
    public void testRegisterSearchProvider_fail_null_all() throws Exception {
        this.engineRegistryService.registerSearchProvider(this.searchEngineProviderMock);
    }

    @Test(expected = Exception.class)
    public void testRegisterSearchProvider_fail_null_engineDescription() throws Exception {
        when(this.searchEngineProviderMock.getFeatureSet()).thenReturn(Mockito.mock(FeatureSet.class, RETURNS_SMART_NULLS));
        when(this.searchEngineProviderMock.newEngineInstance()).thenReturn(Mockito.mock(SearchEngine.class, RETURNS_SMART_NULLS));

        this.engineRegistryService.registerSearchProvider(this.searchEngineProviderMock);
    }

    @Test(expected = Exception.class)
    public void testRegisterSearchProvider_fail_null_engineInstance() throws Exception {
        when(this.searchEngineProviderMock.getFeatureSet()).thenReturn(Mockito.mock(FeatureSet.class, RETURNS_SMART_NULLS));
        when(this.searchEngineProviderMock.getEngineDescription()).thenReturn(Mockito.mock(EngineDescription.class, RETURNS_SMART_NULLS));

        this.engineRegistryService.registerSearchProvider(this.searchEngineProviderMock);
    }

    @Test(expected = Exception.class)
    public void testRegisterSearchProvider_fail_null_featureSet() throws Exception {
        when(this.searchEngineProviderMock.getEngineDescription()).thenReturn(Mockito.mock(EngineDescription.class, RETURNS_SMART_NULLS));
        when(this.searchEngineProviderMock.newEngineInstance()).thenReturn(Mockito.mock(SearchEngine.class, RETURNS_SMART_NULLS));

        this.engineRegistryService.registerSearchProvider(this.searchEngineProviderMock);
    }

    @Test
    public void testRegisterSearchProvider_success() throws Exception {
        // Setup mock to return all non-null
        FeatureSet featureSet = ImmutableFeatureSet.builder()
                .addSupportedBilingualDictionary(BilingualDictionary.fromLanguages(Language.GERMAN, Language.ENGLISH, true))
                .addSupportedLexicographicLanguage(Language.GERMAN)
                .build();
        when(this.searchEngineProviderMock.getEngineDescription()).thenReturn(Mockito.mock(EngineDescription.class, RETURNS_SMART_NULLS));
        when(this.searchEngineProviderMock.getFeatureSet()).thenReturn(featureSet);
        when(this.searchEngineProviderMock.newEngineInstance()).thenReturn(Mockito.mock(SearchEngine.class, RETURNS_SMART_NULLS));

        assertEquals(0, this.engineRegistryService.countRegisteredEngines());
        this.engineRegistryService.registerSearchProvider(this.searchEngineProviderMock);
        assertEquals(1, this.engineRegistryService.countRegisteredEngines());

        assertEquals("Monolingual language has not been registered", 1, this.engineRegistryService.languageEngineNameMap.size());
        assertEquals("Bilingual dictionary has not been registered", 4, this.engineRegistryService.dictionaryEngineNameMap.size());
    }
}