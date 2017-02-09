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
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.api.aggregation.Merges;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;
import org.xlrnet.metadict.core.util.CommonUtils;

import java.util.*;
import java.util.function.Function;

/**
 * <p>Implementation of {@link SimilarElementsMerger} for {@link BilingualEntry} objects. <br/> Uses the following
 * algorithm:</p> <ol> <li>Order source and target language to the same</li> <li>Normalize each generalForm depending on
 * the language and wordtype (This removes e.g. "to" for all english verbs or articles for nouns)</li> <li>Find pairs
 * where the normalized generalForms and wordtype match</li> </ol>
 * <p>Now perform the following operations on the matched objects in each language separately.</p>
 * <p>Processing for DictionaryObjects:<br/> (If more than two objects are being compared to each other, use the most
 * often used)</p> <ol> <li>If the grammatical gender is missing, use the grammatical gender of the other entry. Abort
 * merging if they differ.</li> <li>Merge both descriptions, domain and abbreviation, unless they are equal when
 * normalized (i.e. stripped and lowercased). If they are equal, use the first found.</li> <li>Normalize all additional
 * forms and compare them. If any is missing, add them. If the forms are unequal when normalized, merge them.</li>
 * <li>Merge alternate forms and meanings.</li> <li>Use the most-often occurred pronunciation and syllabification or
 * first non-empty if none is most often.</li </ol>
 */
@Merges(BilingualEntry.class)
public class BilingualEntryMerger implements SimilarElementsMerger<BilingualEntry> {

    private static final String JOINED_ATTRIBUTES_SEPARATOR = ", ";

    private static Function<BilingualEntry, Triple<Pair<Language, Language>, EntryType, Pair<String, String>>> INDEX_FUNCTION = input -> {
        ImmutablePair<Language, Language> languagePair = ImmutablePair.of(input.getSource().getLanguage(), input.getTarget().getLanguage());
        ImmutablePair<String, String> generalFormPair = ImmutablePair.of(input.getSource().getGeneralForm(), input.getTarget().getGeneralForm());
        return Triple.of(languagePair, input.getEntryType(), generalFormPair);
    };

    @NotNull
    @Override
    public Collection<BilingualEntry> merge(@NotNull Collection<BilingualEntry> collectionToMerge) {
        Collection<BilingualEntry> normalizedInput = normalizeInput(collectionToMerge);
        Collection<Collection<BilingualEntry>> candidates = findCandidates(normalizedInput);
        Collection<BilingualEntry> mergedElements = mergeCandidates(candidates);
        return mergedElements;
    }

    /**
     * Normalizes the input collection and makes sure that all entries from the same dictionary are ordered the same.
     */
    @NotNull
    protected Collection<BilingualEntry> normalizeInput(@NotNull Collection<BilingualEntry> collectionToMerge) {
        Collection<BilingualEntry> normalizedInput = new ArrayList<>(collectionToMerge.size());
        Map<Pair<Language, Language>, Language> languageOrderMap = new HashMap<>();

        for (BilingualEntry bilingualEntry : collectionToMerge) {
            Language sourceLanguage = bilingualEntry.getSource().getLanguage();
            Language targetLanguage = bilingualEntry.getTarget().getLanguage();

            Pair<Language, Language> languagePair = Pair.of(sourceLanguage, targetLanguage);
            Language expectedSourceLanguage = languageOrderMap.get(languagePair);
            if (expectedSourceLanguage == null) {
                languageOrderMap.put(languagePair, sourceLanguage);
                expectedSourceLanguage = languageOrderMap.putIfAbsent(Pair.of(targetLanguage, sourceLanguage), sourceLanguage);
            }

            if (expectedSourceLanguage == null || sourceLanguage.equals(expectedSourceLanguage)) {
                // Bilingual entry is already in the correct order
                normalizedInput.add(bilingualEntry);
            } else {
                // Bilingual entry must be inverted
                normalizedInput.add(ImmutableBilingualEntry.invert(bilingualEntry));
            }
        }
        return normalizedInput;
    }

    /**
     * Find potential candidates for merging by grouping elements with the same dictionary, entry type and general form.
     */
    @NotNull
    protected Collection<Collection<BilingualEntry>> findCandidates(@NotNull Collection<BilingualEntry> normalizedInput) {
        Multimap<Triple<Pair<Language, Language>, EntryType, Pair<String, String>>, BilingualEntry> candidatesMap = Multimaps.index(normalizedInput, INDEX_FUNCTION::apply);
        Collection<Collection<BilingualEntry>> candidates = new ArrayList<>(candidatesMap.keys().size());
        for (Triple<Pair<Language, Language>, EntryType, Pair<String, String>> key : candidatesMap.asMap().keySet()) {
            candidates.add(candidatesMap.get(key));
        }
        // TODO: Try to associate objects with unknown entryTypes to objects with known entryType
        return candidates;
    }

    @NotNull
    protected Collection<BilingualEntry> mergeCandidates(@NotNull Collection<Collection<BilingualEntry>> candidates) {
        Collection<BilingualEntry> merged = new ArrayList<>(candidates.size());
        for (Collection<BilingualEntry> candidate : candidates) {
            merged.add(mergeCandidate(candidate));
        }
        return merged;
    }

    @NotNull
    protected BilingualEntry mergeCandidate(@NotNull Collection<BilingualEntry> candidate) {
        BilingualEntryBuilder builder = ImmutableBilingualEntry.builder();
        List<DictionaryObject> sourceObjects = new ArrayList<>(candidate.size());
        List<DictionaryObject> targetObjects = new ArrayList<>(candidate.size());

        boolean isFirst = true;
        for (BilingualEntry bilingualEntry : candidate) {
            if (isFirst) {
                builder.setEntryType(bilingualEntry.getEntryType());
                isFirst = false;
            }
            sourceObjects.add(bilingualEntry.getSource());
            targetObjects.add(bilingualEntry.getTarget());
        }

        DictionaryObject sourceObject = mergeDictionaryObjects(sourceObjects);
        DictionaryObject targetObject = mergeDictionaryObjects(targetObjects);

        builder.setInputObject(sourceObject).setOutputObject(targetObject);
        return builder.build();
    }

    @NotNull
    private DictionaryObject mergeDictionaryObjects(@NotNull List<DictionaryObject> sourceObjects) {
        DictionaryObjectBuilder builder = ImmutableDictionaryObject.builder();
        builder.setLanguage(sourceObjects.get(0).getLanguage());
        builder.setGeneralForm(sourceObjects.get(0).getGeneralForm());

        builder.setGrammaticalGender(CommonUtils.getFirstNotNull(sourceObjects, DictionaryObject::getGrammaticalGender));

        builder.setDescription(mergeAttribute(sourceObjects, DictionaryObject::getDescription));
        builder.setDomain(mergeAttribute(sourceObjects, DictionaryObject::getDomain));
        builder.setAbbreviation(mergeAttribute(sourceObjects, DictionaryObject::getAbbreviation));
        builder.setMeanings(mergeCollectionAttribute(sourceObjects, DictionaryObject::getMeanings));
        builder.setAlternateForms(mergeCollectionAttribute(sourceObjects, DictionaryObject::getAlternateForms));

        // TODO: Include merging for additional forms, pronunciation and syylabification

        return builder.build();
    }

    @Nullable
    private String mergeAttribute(@NotNull List<DictionaryObject> sourceObjects, @NotNull Function<DictionaryObject, String> collector) {
        Map<String, String> normalizedAndActualAttributes = new HashMap<>(sourceObjects.size());
        for (DictionaryObject sourceObject : sourceObjects) {
            String collectedString = collector.apply(sourceObject);
            if (collectedString == null) {
                continue;
            }
            String normalized = simpleNormalize(collectedString);
            normalizedAndActualAttributes.putIfAbsent(normalized, collectedString);
        }

        String joinedAttributes = StringUtils.join(normalizedAndActualAttributes.values(), JOINED_ATTRIBUTES_SEPARATOR);
        return StringUtils.stripToNull(joinedAttributes);
    }

    @Nullable
    private List<String> mergeCollectionAttribute(@NotNull List<DictionaryObject> sourceObjects, @NotNull Function<DictionaryObject, List<String>> collector) {
        Map<String, String> normalizedAndActualAttributes = new HashMap<>(sourceObjects.size());
        for (DictionaryObject sourceObject : sourceObjects) {
            List<String> collected = collector.apply(sourceObject);
            if (collected == null) {
                continue;
            }
            for (String s : collected) {
                String normalized = simpleNormalize(s);
                normalizedAndActualAttributes.putIfAbsent(normalized, s);
            }
        }

        return ImmutableList.sortedCopyOf(normalizedAndActualAttributes.values());
    }

    @NotNull
    private String simpleNormalize(@NotNull String collectedString) {
        return StringUtils.lowerCase(StringUtils.strip(collectedString));
    }
}
