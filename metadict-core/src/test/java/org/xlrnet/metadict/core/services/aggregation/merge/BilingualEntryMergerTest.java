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
import org.junit.Test;
import org.xlrnet.metadict.api.language.*;
import org.xlrnet.metadict.api.query.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * Tests for {@link BilingualEntryMerger}.
 */
public class BilingualEntryMergerTest {

    @Test
    public void normalizeInput() throws Exception {
        DictionaryObject sourceA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        DictionaryObject targetA = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");
        BilingualEntry entryA = ImmutableBilingualEntry.builder().setInputObject(sourceA).setOutputObject(targetA).setEntryType(EntryType.OTHER_WORD).build();

        DictionaryObject sourceB = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "B");
        DictionaryObject targetB = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "B");
        BilingualEntry entryB = ImmutableBilingualEntry.builder().setInputObject(sourceB).setOutputObject(targetB).setEntryType(EntryType.OTHER_WORD).build();

        DictionaryObject sourceC = ImmutableDictionaryObject.createSimpleObject(Language.NORWEGIAN, "C");
        DictionaryObject targetC = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "C");
        BilingualEntry entryC = ImmutableBilingualEntry.builder().setInputObject(sourceC).setOutputObject(targetC).setEntryType(EntryType.OTHER_WORD).build();

        DictionaryObject sourceD = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "D");
        DictionaryObject targetD = ImmutableDictionaryObject.createSimpleObject(Language.NORWEGIAN, "D");
        BilingualEntry entryD = ImmutableBilingualEntry.builder().setInputObject(sourceD).setOutputObject(targetD).setEntryType(EntryType.OTHER_WORD).build();

        ImmutableList<BilingualEntry> toNormalize = ImmutableList.of(entryA, entryB, entryC, entryD);

        Collection<BilingualEntry> normalizedInput = new BilingualEntryMerger().normalizeInput(toNormalize);

        assertNotNull(normalizedInput);
        assertEquals(toNormalize.size(), normalizedInput.size());

        assertTrue(normalizedInput.contains(entryA));
        assertTrue(normalizedInput.contains(ImmutableBilingualEntry.invert(entryB)));
        assertTrue(normalizedInput.contains(entryC));
        assertTrue(normalizedInput.contains(entryD));
    }

    @Test
    public void findCandidates() throws Exception {
        DictionaryObject sourceA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        DictionaryObject targetA = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");
        BilingualEntry entryA = ImmutableBilingualEntry.builder().setInputObject(sourceA).setOutputObject(targetA).setEntryType(EntryType.OTHER_WORD).build();

        DictionaryObject sourceB = ImmutableDictionaryObject.builder().setLanguage(Language.GERMAN).setGeneralForm("A").setDescription("bla").build();
        DictionaryObject targetB = ImmutableDictionaryObject.builder().setLanguage(Language.ENGLISH).setGeneralForm("A").setDescription("bla").build();
        BilingualEntry entryB = ImmutableBilingualEntry.builder().setInputObject(sourceB).setOutputObject(targetB).setEntryType(EntryType.OTHER_WORD).build();

        DictionaryObject sourceC = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        DictionaryObject targetC = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");
        BilingualEntry entryC = ImmutableBilingualEntry.builder().setInputObject(sourceC).setOutputObject(targetC).setEntryType(EntryType.NOUN).build();

        DictionaryObject sourceD = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");
        DictionaryObject targetD = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        BilingualEntry entryD = ImmutableBilingualEntry.builder().setInputObject(sourceD).setOutputObject(targetD).setEntryType(EntryType.OTHER_WORD).build();

        ImmutableList<BilingualEntry> toCandidatize = ImmutableList.of(entryA, entryB, entryC, entryD);

        Collection<Collection<BilingualEntry>> candidates = new BilingualEntryMerger().findCandidates(toCandidatize);

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
        DictionaryObject targetA = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");
        BilingualEntry entryA = ImmutableBilingualEntry.builder().setInputObject(sourceA).setOutputObject(targetA).setEntryType(EntryType.NOUN).build();

        DictionaryObject sourceB = ImmutableDictionaryObject.builder().setLanguage(Language.GERMAN).setGeneralForm("A").setDescription("bla").build();
        DictionaryObject targetB = ImmutableDictionaryObject.builder().setLanguage(Language.ENGLISH).setGeneralForm("A").setDescription("bla").build();
        BilingualEntry entryB = ImmutableBilingualEntry.builder().setInputObject(sourceB).setOutputObject(targetB).setEntryType(EntryType.VERB).build();

        DictionaryObject sourceC = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        DictionaryObject targetC = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");
        BilingualEntry entryC = ImmutableBilingualEntry.builder().setInputObject(sourceC).setOutputObject(targetC).setEntryType(EntryType.UNKNOWN).build();

        ImmutableList<BilingualEntry> toCandidatize = ImmutableList.of(entryA, entryB, entryC);
        Collection<Collection<BilingualEntry>> candidates = new BilingualEntryMerger().findCandidates(toCandidatize);

        assertEquals(3, candidates.size());
        assertTrue(candidates.contains(ImmutableList.of(entryA)));
        assertTrue(candidates.contains(ImmutableList.of(entryB)));
        assertTrue(candidates.contains(ImmutableList.of(entryC)));
    }

    @Test
    public void mergeCandidate_unknown_noElse() {
        DictionaryObject sourceA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        DictionaryObject targetA = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");
        BilingualEntry entryA = ImmutableBilingualEntry.builder().setInputObject(sourceA).setOutputObject(targetA).setEntryType(EntryType.UNKNOWN).build();

        ImmutableList<BilingualEntry> toCandidatize = ImmutableList.of(entryA);
        Collection<Collection<BilingualEntry>> candidates = new BilingualEntryMerger().findCandidates(toCandidatize);

        assertEquals(1, candidates.size());
        assertTrue(candidates.contains(ImmutableList.of(entryA)));
    }

    @Test
    public void mergeCandidate_unknown() {
        DictionaryObject sourceA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        DictionaryObject targetA = ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "A");
        BilingualEntry entryA = ImmutableBilingualEntry.builder().setInputObject(sourceA).setOutputObject(targetA).setEntryType(EntryType.NOUN).build();

        DictionaryObject sourceB = ImmutableDictionaryObject.builder().setLanguage(Language.GERMAN).setGeneralForm("A").setDescription("bla").build();
        DictionaryObject targetB = ImmutableDictionaryObject.builder().setLanguage(Language.ENGLISH).setGeneralForm("A").setDescription("bla").build();
        BilingualEntry entryB = ImmutableBilingualEntry.builder().setInputObject(sourceB).setOutputObject(targetB).setEntryType(EntryType.UNKNOWN).build();

        ImmutableList<BilingualEntry> toCandidatize = ImmutableList.of(entryA, entryB);
        Collection<Collection<BilingualEntry>> candidates = new BilingualEntryMerger().findCandidates(toCandidatize);

        BilingualEntry entryBexpected = ImmutableBilingualEntry.builder().setInputObject(sourceB).setOutputObject(targetB).setEntryType(EntryType.NOUN).build();

        assertEquals(1, candidates.size());
        assertTrue(candidates.contains(ImmutableList.of(entryA, entryBexpected)));
    }

    @Test
    public void mergeCandidate_unknownWithDialect() {
        DictionaryObject sourceA = ImmutableDictionaryObject.createSimpleObject(Language.GERMAN, "A");
        DictionaryObject targetA = ImmutableDictionaryObject.createSimpleObject(Language.getLanguageById("en_uk"), "A");
        BilingualEntry entryA = ImmutableBilingualEntry.builder().setInputObject(sourceA).setOutputObject(targetA).setEntryType(EntryType.NOUN).build();

        DictionaryObject sourceB = ImmutableDictionaryObject.builder().setLanguage(Language.GERMAN).setGeneralForm("A").setDescription("bla").build();
        DictionaryObject targetB = ImmutableDictionaryObject.builder().setLanguage(Language.ENGLISH).setGeneralForm("A").setDescription("bla").build();
        BilingualEntry entryB = ImmutableBilingualEntry.builder().setInputObject(sourceB).setOutputObject(targetB).setEntryType(EntryType.UNKNOWN).build();

        ImmutableList<BilingualEntry> toCandidatize = ImmutableList.of(entryA, entryB);
        Collection<Collection<BilingualEntry>> candidates = new BilingualEntryMerger().findCandidates(toCandidatize);

        BilingualEntry entryBexpected = ImmutableBilingualEntry.builder().setInputObject(sourceB).setOutputObject(targetB).setEntryType(EntryType.NOUN).build();

        assertEquals(1, candidates.size());
        assertTrue(candidates.contains(ImmutableList.of(entryA, entryBexpected)));
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

        DictionaryObject candidateTargetA = ImmutableDictionaryObject.builder()
                .setLanguage(Language.ENGLISH)
                .setGeneralForm("dog")
                .setDescription("animal")
                .setGrammaticalGender(null)
                .setAbbreviation("D.")
                .setDomain("household")
                .setAlternateForms(ImmutableList.of("doggy", "doggo"))
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

        DictionaryObject candidateTargetB = ImmutableDictionaryObject.builder()
                .setLanguage(Language.ENGLISH)
                .setGeneralForm("dog")
                .setDescription("  Animal  ")
                .setGrammaticalGender(null)
                .setAbbreviation("d.")
                .setAlternateForms(ImmutableList.of("Doggo"))
                .build();

        BilingualEntry candidateA = ImmutableBilingualEntry.builder().setInputObject(candidateSourceA).setOutputObject(candidateTargetA).setEntryType(EntryType.NOUN).build();
        BilingualEntry candidateB = ImmutableBilingualEntry.builder().setInputObject(candidateSourceB).setOutputObject(candidateTargetB).setEntryType(EntryType.NOUN).build();

        List<BilingualEntry> candidates = Lists.newArrayList(candidateA, candidateB);

        BilingualEntry actualMerged = new BilingualEntryMerger().mergeCandidate(candidates);

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

        DictionaryObject expectedTarget = ImmutableDictionaryObject.builder()
                .setLanguage(Language.ENGLISH)
                .setGeneralForm("dog")
                .setDescription("animal")
                .setAbbreviation("D.")
                .setDomain("household")
                .setAlternateForms(ImmutableList.of("doggo", "doggy"))
                .build();
        BilingualEntry expected = ImmutableBilingualEntry.builder().setInputObject(expectedSource).setOutputObject(expectedTarget).setEntryType(EntryType.NOUN).build();

        assertEquals(expected, actualMerged);
    }

    /**
     * If only one language of two same words on one side is a dialect, the result must become a dialect.
     */
    @Test
    public void mergeSamePair_oneSideOneDialect() {
        BilingualEntry firstEntry = ImmutableBilingualEntry.builder()
                .setInputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.NORWEGIAN, "trykker"))
                .setOutputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.GERMAN, "Drucker")
                ).build();
        BilingualEntry secondEntry = ImmutableBilingualEntry.builder()
                .setInputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.GERMAN, "Drucker"))
                .setOutputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.NORWEGIAN_BOKMÅL, "trykker")
                ).build();

        BilingualEntry expected = ImmutableBilingualEntry.builder()
                .setInputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.NORWEGIAN_BOKMÅL, "trykker"))
                .setOutputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.GERMAN, "Drucker")
                ).build();

        ArrayList<BilingualEntry> collectionToMerge = Lists.newArrayList(firstEntry, secondEntry);
        Collection<BilingualEntry> actual = new BilingualEntryMerger().merge(collectionToMerge);

        ArrayList<BilingualEntry> actualList = Lists.newArrayList(actual);

        assertEquals("Expected input objects to be merged", 1, actualList.size());
        assertEquals(expected, actualList.get(0));
    }

    /**
     * If both languages of the same word on one side belong to different dialects, they may not be merged.
     */
    @Test
    public void mergeSamePair_oneSideTwoDifferentDialect() {
        BilingualEntry firstEntry = ImmutableBilingualEntry.builder()
                .setInputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.NORWEGIAN_NYNORSK, "trykker"))
                .setOutputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.GERMAN, "Drucker")
                ).build();
        BilingualEntry secondEntry = ImmutableBilingualEntry.builder()
                .setInputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.GERMAN, "Drucker"))
                .setOutputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.NORWEGIAN_BOKMÅL, "trykker")
                ).build();

        ArrayList<BilingualEntry> collectionToMerge = Lists.newArrayList(firstEntry, secondEntry);
        Collection<BilingualEntry> actual = new BilingualEntryMerger().merge(collectionToMerge);

        ArrayList<BilingualEntry> actualList = Lists.newArrayList(actual);

        assertEquals("Expected input objects to be not merged", 2, actualList.size());
    }

    /**
     * If both languages of the same word on both sides belong to different dialects, they may not be merged.
     */
    @Test
    public void mergeSamePair_bothSidesTwoDifferentDialect() {
        BilingualEntry firstEntry = ImmutableBilingualEntry.builder()
                .setInputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.NORWEGIAN_NYNORSK, "trykker"))
                .setOutputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.getLanguageById("de_sv"), "Drucker")
                ).build();
        BilingualEntry secondEntry = ImmutableBilingualEntry.builder()
                .setInputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.getLanguageById("de_sx"), "Drucker"))
                .setOutputObject(
                        ImmutableDictionaryObject.createSimpleObject(
                                Language.NORWEGIAN_BOKMÅL, "trykker")
                ).build();

        ArrayList<BilingualEntry> collectionToMerge = Lists.newArrayList(firstEntry, secondEntry);
        Collection<BilingualEntry> actual = new BilingualEntryMerger().merge(collectionToMerge);

        ArrayList<BilingualEntry> actualList = Lists.newArrayList(actual);

        assertEquals("Expected input objects to be not merged", 2, actualList.size());
    }

}