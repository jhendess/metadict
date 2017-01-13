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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xlrnet.metadict.api.language.*;
import org.xlrnet.metadict.api.query.DictionaryObjectBuilder;

import static org.mockito.Mockito.verify;

/**
 * Test cases for the Heinzelnisse engine.
 */
public class HeinzelnisseEngineTest {

    private static final String TEST_PLURAL = "Plural: Mütter";

    private static final String TEST_FLERTALL = "fl.: mødre";

    private static final String TEST_SYNONYM = "syn.: tschüs";

    private static final String TEST_DIALECT = "Dialekt (süddeutsch/österreichisch)";

    private static final String TEST_TENSES = "presens: kommer, preteritum: kom, partisipp perfekt: kommet";

    private static final String TEST_ADJECTIVE_FORMS = "bestemt form: lille, intetkjønn: lite, flertall: små";

    private static final String TEST_COMPARISON_FORMS = "Komparativ: weniger, Superlativ: am wenigsten";

    private static final String ANSWER_TEST_PLURAL = "Mütter";

    private static final String ANSWER_TEST_FLERTALL = "mødre";

    private HeinzelnisseEngine engine = new HeinzelnisseEngine();

    private DictionaryObjectBuilder dictionaryObjectBuilder;

    @Before
    public void setUp() throws Exception {
        dictionaryObjectBuilder = Mockito.mock(DictionaryObjectBuilder.class);
    }

    @Test
    public void testExtractOtherInformation_adjectiveForms() throws Exception {
        engine.extractOtherInformation(TEST_ADJECTIVE_FORMS, dictionaryObjectBuilder);
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalCase.DEFINITE_FORM, "lille");
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalGender.NEUTER, "lite");
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalNumber.PLURAL, "små");
    }

    @Test
    public void testExtractOtherInformation_comparison() throws Exception {
        engine.extractOtherInformation(TEST_COMPARISON_FORMS, dictionaryObjectBuilder);
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalComparison.COMPARATIVE, "weniger");
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalComparison.SUPERLATIVE, "am wenigsten");
    }

    @Test
    public void testExtractOtherInformation_dialekt() throws Exception {
        engine.extractOtherInformation(TEST_DIALECT, dictionaryObjectBuilder);
        verify(dictionaryObjectBuilder).setDescription(TEST_DIALECT);
    }

    @Test
    public void testExtractOtherInformation_flertall() throws Exception {
        engine.extractOtherInformation(TEST_FLERTALL, dictionaryObjectBuilder);
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalNumber.PLURAL, ANSWER_TEST_FLERTALL);
    }

    @Test
    public void testExtractOtherInformation_plural() throws Exception {
        engine.extractOtherInformation(TEST_PLURAL, dictionaryObjectBuilder);
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalNumber.PLURAL, ANSWER_TEST_PLURAL);
    }

    @Test
    public void testExtractOtherInformation_syn() throws Exception {
        engine.extractOtherInformation(TEST_SYNONYM, dictionaryObjectBuilder);
        verify(dictionaryObjectBuilder).setDescription("syn.: tschüs");

    }

    @Test
    public void testExtractOtherInformation_tenses() throws Exception {
        engine.extractOtherInformation(TEST_TENSES, dictionaryObjectBuilder);
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalTense.PRESENT_TENSE, "kommer");
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalTense.PAST_TENSE, "kom");
        verify(dictionaryObjectBuilder).setAdditionalForm(GrammaticalTense.PERFECT_PARTICIPLE, "kommet");
    }
}