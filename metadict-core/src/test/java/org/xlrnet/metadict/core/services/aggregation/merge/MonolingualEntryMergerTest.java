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
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.xlrnet.metadict.api.language.*;
import org.xlrnet.metadict.api.query.*;

import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests for {@link MonolingualEntryMerger}.
 */
public class MonolingualEntryMergerTest {

    private MonolingualEntryMerger merger;

    @Before
    public void setup() {
        merger = new MonolingualEntryMerger(null);  // Normalizer not necessary for tests
    }

    @Test
    public void findCandidates() throws Exception {
        DictionaryObject sourceA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        MonolingualEntry entryA = ImmutableMonolingualEntry.builder().setContent(sourceA).setEntryType(EntryType.OTHER_WORD).build();

        DictionaryObject sourceB = ImmutableDictionaryObject.builder().setLanguage(Language.GERMAN).setGeneralForm("A").setDescription("bla").build();
        MonolingualEntry entryB = ImmutableMonolingualEntry.builder().setContent(sourceB).setEntryType(EntryType.OTHER_WORD).build();

        DictionaryObject sourceC = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "a");
        MonolingualEntry entryC = ImmutableMonolingualEntry.builder().setContent(sourceC).setEntryType(EntryType.NOUN).build();

        DictionaryObject sourceD = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");
        MonolingualEntry entryD = ImmutableMonolingualEntry.builder().setContent(sourceD).setEntryType(EntryType.NOUN).build();

        ImmutableList<MonolingualEntry> toCandidatize = ImmutableList.of(entryA, entryB, entryC, entryD);

        Collection<Collection<MonolingualEntry>> candidates = merger.findCandidates(toCandidatize);

        assertEquals(3, candidates.size());
        assertTrue(candidates.contains(ImmutableList.of(entryA, entryB)));
        assertTrue(candidates.contains(ImmutableList.of(entryC)));
        assertTrue(candidates.contains(ImmutableList.of(entryD)));
    }

    /**
     * Contains one unknown and two other entryTypes. In this case, objects may not be treated as candidates of the same
     * group.
     */
    @Test
    public void mergeCandidate_unknownDouble() {
        DictionaryObject sourceA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        MonolingualEntry entryA = ImmutableMonolingualEntry.builder().setContent(sourceA).setEntryType(EntryType.NOUN).build();

        DictionaryObject sourceB = ImmutableDictionaryObject.builder().setLanguage(Language.GERMAN).setGeneralForm("A").setDescription("bla").build();
        MonolingualEntry entryB = ImmutableMonolingualEntry.builder().setContent(sourceB).setEntryType(EntryType.VERB).build();

        DictionaryObject sourceC = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        MonolingualEntry entryC = ImmutableMonolingualEntry.builder().setContent(sourceC).
                setEntryType(EntryType.UNKNOWN).build();

        ImmutableList<MonolingualEntry> toCandidatize = ImmutableList.of(entryA, entryB, entryC);
        Collection<Collection<MonolingualEntry>> candidates = merger.findCandidates(toCandidatize);

        assertEquals(3, candidates.size());
        assertTrue(candidates.contains(ImmutableList.of(entryA)));
        assertTrue(candidates.contains(ImmutableList.of(entryB)));
        assertTrue(candidates.contains(ImmutableList.of(entryC)));
    }

    @Test
    public void mergeCandidate_unknown_noElse() {
        DictionaryObject sourceA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        MonolingualEntry entryA = ImmutableMonolingualEntry.builder().setContent(sourceA).setEntryType(EntryType.UNKNOWN).build();

        ImmutableList<MonolingualEntry> toCandidatize = ImmutableList.of(entryA);
        Collection<Collection<MonolingualEntry>> candidates = merger.findCandidates(toCandidatize);

        assertEquals(1, candidates.size());
        assertTrue(candidates.contains(ImmutableList.of(entryA)));
    }

    @Test
    public void mergeCandidate_unknown() {
        DictionaryObject sourceA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        MonolingualEntry entryA = ImmutableMonolingualEntry.builder().setContent(sourceA).setEntryType(EntryType.NOUN).build();

        DictionaryObject sourceB = ImmutableDictionaryObject.builder().setLanguage(Language.GERMAN).setGeneralForm("A").setDescription("bla").build();
        MonolingualEntry entryB = ImmutableMonolingualEntry.builder().setContent(sourceB).setEntryType(EntryType.UNKNOWN).build();

        ImmutableList<MonolingualEntry> toCandidatize = ImmutableList.of(entryA, entryB);
        Collection<Collection<MonolingualEntry>> candidates = merger.findCandidates(toCandidatize);

        assertEquals(1, candidates.size());
        assertTrue(candidates.contains(ImmutableList.of(entryA, entryB)));
    }

    @Test
    public void mergeCandidate() throws Exception {
        String[] syllabification = {"Hund"};

        DictionaryObject candidateSourceA = ImmutableDictionaryObject.builder()
                .setLanguage(Language.GERMAN)
                .setGeneralForm("Hund")
                .setDescription("Tier")
                .setGrammaticalGender(null)
                .setAbbreviation("H.")
                .setDomain("Haushalt")
                .setMeanings(ImmutableList.of("Haustier", "Hund"))
                .setAdditionalForm(new NounForm(GrammaticalCase.NOMINATIVE, GrammaticalNumber.PLURAL, GrammaticalGender.MASCULINE), "Hunde")
                .setAdditionalForm(new NounForm(GrammaticalCase.GENITIVE, GrammaticalNumber.SINGULAR, null), "Hunds")
                .build();

        DictionaryObject candidateSourceB = ImmutableDictionaryObject.builder()
                .setLanguage(Language.GERMAN)
                .setGeneralForm("hund")
                .setDescription("Säugetier")
                .setGrammaticalGender(GrammaticalGender.MASCULINE)
                .setAbbreviation("hu.")
                .setDomain("Zool.")
                .setMeanings(ImmutableList.of("hund"))
                .setAdditionalForm(new NounForm(GrammaticalCase.NOMINATIVE, GrammaticalNumber.PLURAL, GrammaticalGender.MASCULINE), "hunde")
                .setAdditionalForm(new NounForm(GrammaticalCase.GENITIVE, GrammaticalNumber.SINGULAR, null), "Hundes")
                .setSyllabification(syllabification)
                .build();

        MonolingualEntry candidateA = ImmutableMonolingualEntry.builder().setContent(candidateSourceA).setEntryType(EntryType.NOUN).build();
        MonolingualEntry candidateB = ImmutableMonolingualEntry.builder().setContent(candidateSourceB).setEntryType(EntryType.NOUN).build();

        List<MonolingualEntry> candidates = Lists.newArrayList(candidateA, candidateB);

        MonolingualEntry actualMerged = merger.mergeCandidate(candidates);

        DictionaryObject expectedSource = ImmutableDictionaryObject.builder()
                .setLanguage(Language.GERMAN)
                .setGeneralForm("Hund")
                .setDescription("Tier, Säugetier")
                .setGrammaticalGender(GrammaticalGender.MASCULINE)
                .setAbbreviation("hu., H.")
                .setDomain("Haushalt, Zool.")
                .setMeanings(ImmutableList.of("Haustier", "Hund"))
                // Normalized duplicates should be reduced
                .setAdditionalForm(new NounForm(GrammaticalCase.NOMINATIVE, GrammaticalNumber.PLURAL, GrammaticalGender.MASCULINE), "Hunde")
                // Additional values should be merged
                .setAdditionalForm(new NounForm(GrammaticalCase.GENITIVE, GrammaticalNumber.SINGULAR, null), "Hundes, Hunds")
                .setSyllabification(syllabification)
                .build();

        MonolingualEntry expected = ImmutableMonolingualEntry.builder().setContent(expectedSource).setEntryType(EntryType.NOUN).build();

        assertEquals(expected, actualMerged);

    }

}