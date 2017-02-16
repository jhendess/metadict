/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
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

package org.xlrnet.metadict.core.services.aggregation.merge;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ImmutableDictionaryObject;

import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests for {@link DictionaryObjectMerger}.
 */
public class DictionaryObjectMergerTest {

    @Test
    public void findCandidates() throws Exception {
        DictionaryObject objectA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        DictionaryObject objectB = ImmutableDictionaryObject.builder().setLanguage(Language.GERMAN).setGeneralForm("A").setDescription("bla").build();
        DictionaryObject objectC = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "a");
        DictionaryObject objectD = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");

        ImmutableList<DictionaryObject> toCandidatize = ImmutableList.of(objectA, objectB, objectC, objectD);

        Collection<Collection<DictionaryObject>> candidates = new DictionaryObjectMerger().findCandidates(toCandidatize);

        assertEquals(2, candidates.size());
        assertTrue(candidates.contains(ImmutableList.of(objectA, objectB, objectC)));
        assertTrue(candidates.contains(ImmutableList.of(objectD)));
    }
}