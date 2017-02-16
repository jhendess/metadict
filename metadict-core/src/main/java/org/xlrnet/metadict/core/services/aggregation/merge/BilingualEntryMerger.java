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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.language.GrammaticalForm;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.api.aggregation.Merges;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;
import org.xlrnet.metadict.core.util.CollectionUtils;
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

    /**
     * Function which is used  for indexing {@link BilingualEntry} objects into a map.
     */
    @NotNull
    private static MergeCandidateIdentifier buildIdentifierFromBilingualEntry(@NotNull BilingualEntry input) {
        ImmutablePair<Language, Language> languagePair = ImmutablePair.of(input.getSource().getLanguage(), input.getTarget().getLanguage());
        ImmutablePair<String, String> generalFormPair = ImmutablePair.of(CommonUtils.simpleNormalize(input.getSource().getGeneralForm()), CommonUtils.simpleNormalize(input.getTarget().getGeneralForm()));
        return new MergeCandidateIdentifier(languagePair, input.getEntryType(), generalFormPair);
    }

    @NotNull
    @Override
    public Collection<BilingualEntry> merge(@NotNull Collection<BilingualEntry> collectionToMerge) {
        Collection<BilingualEntry> normalizedInput = normalizeInput(collectionToMerge);
        Collection<Collection<BilingualEntry>> candidates = findCandidates(normalizedInput);
        return mergeCandidates(candidates);
    }

    /**
     * Normalizes the input collection and makes sure that all entries from the same dictionary are ordered the same.
     */
    @NotNull
    Collection<BilingualEntry> normalizeInput(@NotNull Collection<BilingualEntry> collectionToMerge) {
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
    Collection<Collection<BilingualEntry>> findCandidates(@NotNull Collection<BilingualEntry> normalizedInput) {
        Multimap<MergeCandidateIdentifier, BilingualEntry> candidatesMap = buildMergeCandidateMultimap(normalizedInput);
        Multimap<MergeCandidateIdentifier, BilingualEntry> knownMap = Multimaps.filterEntries(candidatesMap, this::toNullIfUnknown);
        Multimap<MergeCandidateIdentifier, BilingualEntry> unknownMap = Multimaps.filterEntries(candidatesMap, this::toNullIfKnown);
        Collection<Collection<BilingualEntry>> candidates = new ArrayList<>(candidatesMap.keys().size());

        identifyUnknownEntryTypeCandidates(candidatesMap, knownMap, unknownMap, candidates);

        for (MergeCandidateIdentifier key : knownMap.asMap().keySet()) {
            candidates.add(candidatesMap.get(key));
        }

        return candidates;
    }

    @NotNull
    private Multimap<MergeCandidateIdentifier, BilingualEntry> buildMergeCandidateMultimap(@NotNull Collection<BilingualEntry> normalizedInput) {
        Multimap<MergeCandidateIdentifier, BilingualEntry> candidatesMap = MultimapBuilder.hashKeys(normalizedInput.size()).linkedListValues().build();
        for (BilingualEntry bilingualEntry : normalizedInput) {
            candidatesMap.put(buildIdentifierFromBilingualEntry(bilingualEntry), bilingualEntry);
        }
        return candidatesMap;
    }

    /**
     * Try to detect the EntryType of unknown objects automatically by checking if an identifier with the same language
     * and general forms parameter exists in another EntryType. If exactly one possible key could be found, then add the
     * unknown entry to it as another candidate. If more than one candidates exist, treat the unknown object as actually
     * unknown.
     */
    private void identifyUnknownEntryTypeCandidates(@NotNull Multimap<MergeCandidateIdentifier, BilingualEntry> candidatesMap,
                                                    @NotNull Multimap<MergeCandidateIdentifier, BilingualEntry> knownMap,
                                                    @NotNull Multimap<MergeCandidateIdentifier, BilingualEntry> unknownMap,
                                                    @NotNull Collection<Collection<BilingualEntry>> candidates) {
        for (MergeCandidateIdentifier unknownTypeKey : unknownMap.asMap().keySet()) {
            Collection<BilingualEntry> values = candidatesMap.get(unknownTypeKey);
            MergeCandidateIdentifier newCandidateIdentifier = null;
            boolean isAdded = false;
            for (EntryType entryType : EntryType.allButUnknown()) {
                MergeCandidateIdentifier candidateIdentifier = new MergeCandidateIdentifier(unknownTypeKey.getLanguagePair(), entryType, unknownTypeKey.getGeneralForms());
                if (knownMap.containsKey(candidateIdentifier)) {
                    if (newCandidateIdentifier != null) {
                        // If two possible EntryTypes exist, then abort automatic discovery and treat unknown as third
                        candidates.add(values);
                        isAdded = true;
                        break;
                    } else {
                        newCandidateIdentifier = candidateIdentifier;
                    }
                }
            }
            // Treat the candidates either again as unknown or add it to the possible identified entryType group
            if (newCandidateIdentifier == null) {
                candidates.add(values);
            } else if (!isAdded) {
                knownMap.putAll(newCandidateIdentifier, values);
            }
        }
    }

    private boolean toNullIfKnown(@Nullable Map.Entry<MergeCandidateIdentifier, BilingualEntry> input) {
        return (input != null ? input.getKey().getEntryType() : null) == EntryType.UNKNOWN;
    }

    private boolean toNullIfUnknown(@Nullable Map.Entry<MergeCandidateIdentifier, BilingualEntry> input) {
        return (input != null ? input.getKey().getEntryType() : null) != EntryType.UNKNOWN;
    }

    @NotNull
    BilingualEntry mergeCandidate(@NotNull Collection<BilingualEntry> candidate) {
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
    private Collection<BilingualEntry> mergeCandidates(@NotNull Collection<Collection<BilingualEntry>> candidates) {
        Collection<BilingualEntry> merged = new ArrayList<>(candidates.size());
        for (Collection<BilingualEntry> candidate : candidates) {
            merged.add(mergeCandidate(candidate));
        }
        return merged;
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
        builder.setPronunciation(mergeAttribute(sourceObjects, DictionaryObject::getPronunciation));
        builder.setMeanings(CollectionUtils.divideAndFilterNormalized(sourceObjects, DictionaryObject::getMeanings, CommonUtils::simpleNormalize));
        builder.setAlternateForms(CollectionUtils.divideAndFilterNormalized(sourceObjects, DictionaryObject::getAlternateForms, CommonUtils::simpleNormalize));

        Map<GrammaticalForm, String> mergedAdditionalForms = CollectionUtils.divideAndMerge(
                sourceObjects,
                DictionaryObject::getAdditionalForms,
                CommonUtils::simpleNormalize,
                ((v, r) -> StringUtils.stripToEmpty(v) + (r != null ? ", " + r : ""))
        );
        builder.setAdditionalForms(mergedAdditionalForms);
        setFirstNonEmptySyllabificationInBuilder(sourceObjects, builder);

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
            String normalized = CommonUtils.simpleNormalize(collectedString);
            normalizedAndActualAttributes.putIfAbsent(normalized, collectedString);
        }

        String joinedAttributes = StringUtils.join(normalizedAndActualAttributes.values(), JOINED_ATTRIBUTES_SEPARATOR);
        return StringUtils.stripToNull(joinedAttributes);
    }

    private void setFirstNonEmptySyllabificationInBuilder(@NotNull List<DictionaryObject> sourceObjects, @NotNull DictionaryObjectBuilder builder) {
        for (DictionaryObject sourceObject : sourceObjects) {
            List<String> syllabification = sourceObject.getSyllabification();
            if (!syllabification.isEmpty()) {
                builder.setSyllabification(syllabification);
                break;
            }
        }
    }

}
