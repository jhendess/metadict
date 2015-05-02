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

package org.xlrnet.metadict.web.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.xlrnet.metadict.api.language.*;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.DictionaryObjectBuilder;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for verifying correct JSON mapping behaviour.
 */
public class ObjectMappingTests {

    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        objectMapper = JacksonResteasyProducer.getObjectMapper();
    }

    @Test
    public void testDictionaryObjectMapping_empty() throws Exception{
        DictionaryObject testObject = prepareDictionaryObjectBuilder().build();

        String actualRepresentation = objectMapper.writeValueAsString(testObject);
        assertThat(actualRepresentation, not(containsString(DictionaryObjectSerializer.ADDITIONAL_REPRESENTATION_FIELD_NAME)));
    }

    @Test
    public void testDictionaryObjectMapping_full() throws Exception{
        DictionaryObject testObject = prepareDictionaryObjectBuilder()
                .setAbbreviation("abbreviation")
                .setDescription("description")
                .setDomain("domain")
                .setGrammaticalGender(GrammaticalGender.FEMININE)
                .setAdditionalForm(GrammaticalCase.DEFINITE_FORM, "definite")
                .setAdditionalForm(GrammaticalNumber.PLURAL, "plural")
                .setAdditionalForm(GrammaticalNumber.SINGULAR, "singular")
                .setAdditionalForm(GrammaticalTense.PAST_PERFECT, "past_perfect")
                .setAdditionalForm(GrammaticalTense.PAST_TENSE, "past_tense")
                .setAdditionalForm(GrammaticalTense.PRESENT_TENSE, "present_tense")
                .setAdditionalForm(GrammaticalTense.PERFECT_PARTICIPLE, "perfect_participle")
                .setAdditionalForm(GrammaticalComparison.POSITIVE, "positive")
                .setAdditionalForm(GrammaticalComparison.COMPARATIVE, "comparative")
                .setAdditionalForm(GrammaticalComparison.SUPERLATIVE, "superlative")
                .build();

        String expectedString = "\"" + DictionaryObjectSerializer.ADDITIONAL_REPRESENTATION_FIELD_NAME + "\""
                + " : \""
                + "description, "
                + "sg.: singular, "
                + "pl.: plural, "
                + "pr.: present_tense, "
                + "pa.: past_tense, "
                + "par.: past_perfect, "
                + "per.: perfect_participle, "
                + "pos.: positive, "
                + "comp.: comparative, "
                + "sup.: superlative, "
                + "def.: definite, "
                + "abbr.: abbreviation, "
                + "dom.: domain, "
                + "(feminine)\"";

        String actualRepresentation = objectMapper.writeValueAsString(testObject);
        assertThat(actualRepresentation, containsString(expectedString));
    }

    private DictionaryObjectBuilder prepareDictionaryObjectBuilder() {
        return new DictionaryObjectBuilder()
                .setGeneralForm("someGeneralForm")
                .setLanguage(Language.GERMAN);
    }

}