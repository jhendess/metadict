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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.Entry;
import org.xlrnet.metadict.api.query.EntryType;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract {@link SimilarElementsMerger} for objects {@link Entry}. <br/> Uses the following algorithm:</p> <ol>
 * <li>Order source and target language to the same</li> <li>Normalize each generalForm depending on the language and
 * wordtype (This removes e.g. "to" for all english verbs or articles for nouns)</li> <li>Find pairs where the
 * normalized generalForms and wordtype match</li> </ol>
 */
public abstract class AbstractEntryMerger<T extends Entry> extends AbstractMerger<T> implements SimilarElementsMerger<T> {


    @NotNull
    protected abstract MergeCandidateIdentifier buildCandidateIdentifier(@NotNull T input);

    @NotNull
    protected Multimap<MergeCandidateIdentifier, T> buildMergeCandidateMultimap(@NotNull Collection<T> normalizedInput) {
        Multimap<MergeCandidateIdentifier, T> candidatesMap = MultimapBuilder.linkedHashKeys(normalizedInput.size()).linkedListValues().build();
        for (T candidate : normalizedInput) {
            candidatesMap.put(buildCandidateIdentifier(candidate), candidate);
        }
        return candidatesMap;
    }

    /**
     * Find potential candidates for merging by grouping elements with the same dictionary, entry type and general
     * form.
     */
    @Override
    @NotNull
    protected Collection<Collection<T>> findCandidates(@NotNull Collection<T> normalizedInput) {
        Multimap<MergeCandidateIdentifier, T> candidatesMap = buildMergeCandidateMultimap(normalizedInput);
        Multimap<MergeCandidateIdentifier, T> knownMap = Multimaps.filterEntries(candidatesMap, this::toNullIfUnknown);
        Multimap<MergeCandidateIdentifier, T> unknownMap = Multimaps.filterEntries(candidatesMap, this::toNullIfKnown);
        Collection<Collection<T>> candidates = new ArrayList<>(candidatesMap.keys().size());

        identifyUnknownEntryTypeCandidates(candidatesMap, knownMap, unknownMap, candidates);

        for (MergeCandidateIdentifier key : knownMap.asMap().keySet()) {
            candidates.add(candidatesMap.get(key));
        }

        return candidates;
    }

    /**
     * Try to detect the EntryType of unknown objects automatically by checking if an identifier with the same language
     * and general forms parameter exists in another EntryType. If exactly one possible key could be found, then add the
     * unknown entry to it as another candidate. If more than one candidates exist, treat the unknown object as actually
     * unknown.
     */
    void identifyUnknownEntryTypeCandidates(@NotNull Multimap<MergeCandidateIdentifier, T> candidatesMap,
                                            @NotNull Multimap<MergeCandidateIdentifier, T> knownMap,
                                            @NotNull Multimap<MergeCandidateIdentifier, T> unknownMap,
                                            @NotNull Collection<Collection<T>> candidates) {
        Multimap<MergeCandidateIdentifier, T> normalizedKnownMap = buildNormalizedCandidateMap(knownMap);
        Set<MergeCandidateIdentifier> toRemove = new HashSet<>();
        List<Pair<MergeCandidateIdentifier, Collection<T>>> toAdd = new ArrayList<>();
        for (MergeCandidateIdentifier unknownTypeKey : unknownMap.asMap().keySet()) {
            Collection<T> values = candidatesMap.get(unknownTypeKey);
            EntryType newEntryType = null;
            boolean isAdded = false;
            for (EntryType entryType : EntryType.allButUnknown()) {
                ImmutablePair<Language, Language> normalizedLanguagePair = getLanguagePairWithoutDialect(unknownTypeKey.getLanguagePair());
                MergeCandidateIdentifier candidateIdentifier = new MergeCandidateIdentifier(normalizedLanguagePair, entryType, unknownTypeKey.getGeneralForms());
                if (normalizedKnownMap.containsKey(candidateIdentifier)) {
                    if (newEntryType != null) {
                        // If two possible EntryTypes exist, then abort automatic discovery and treat unknown as third
                        candidates.add(values);
                        isAdded = true;
                        break;
                    } else {
                        newEntryType = entryType;
                    }
                }
            }
            // Treat the candidates either again as unknown or add it to the possible identified entryType group
            if (newEntryType == null) {
                candidates.add(values); // -> Automatic identification failed
            } else if (!isAdded) {
                // Rebuild the old identifier with changed dialect
                MergeCandidateIdentifier enhancedIdentifier = new MergeCandidateIdentifier(unknownTypeKey.getLanguagePair(), newEntryType, unknownTypeKey.getGeneralForms());
                final EntryType tmp = newEntryType;
                toAdd.add(new ImmutablePair<>(enhancedIdentifier, values.stream().map(v -> enrichEntryByEntryType(v, tmp)).collect(Collectors.toList())));
                toRemove.add(unknownTypeKey);
            }
        }

        for (Pair<MergeCandidateIdentifier, Collection<T>> toAddData : toAdd) {
            knownMap.putAll(toAddData.getKey(), toAddData.getValue());
        }

        for (MergeCandidateIdentifier candidateIdentifier : toRemove) {
            candidatesMap.removeAll(candidateIdentifier);
        }
    }

    /**
     * Creates a new entry with the given entry type.
     */
    protected abstract T enrichEntryByEntryType(T entry, EntryType entryType);

    /**
     * Create a candidate multimap where the languages are normalized (i.e. without dialect).
     */
    private Multimap<MergeCandidateIdentifier, T> buildNormalizedCandidateMap(@NotNull Multimap<MergeCandidateIdentifier, T> knownMap) {
        Multimap<MergeCandidateIdentifier, T> normalizedKnownMap = MultimapBuilder.linkedHashKeys(knownMap.size()).linkedListValues().build();
        for (MergeCandidateIdentifier candidateIdentifier : knownMap.keySet()) {
            Pair<Language, Language> originalLanguagePair = candidateIdentifier.getLanguagePair();
            ImmutablePair<Language, Language> languagePairWithoutDialect = getLanguagePairWithoutDialect(originalLanguagePair);
            normalizedKnownMap.putAll(new MergeCandidateIdentifier(languagePairWithoutDialect, candidateIdentifier.getEntryType(), candidateIdentifier.getGeneralForms()), knownMap.get(candidateIdentifier));
        }
        return normalizedKnownMap;
    }

    @NotNull
    private ImmutablePair<Language, Language> getLanguagePairWithoutDialect(Pair<Language, Language> originalLanguagePair) {
        return new ImmutablePair<>(
                Language.getWithoutDialect(originalLanguagePair.getLeft()),
                // For monolingual pairs, the right language may be null
                originalLanguagePair.getRight() != null ? Language.getWithoutDialect(originalLanguagePair.getRight()) : null);
    }

    boolean toNullIfKnown(@Nullable Map.Entry<MergeCandidateIdentifier, T> input) {
        return (input != null ? input.getKey().getEntryType() : null) == EntryType.UNKNOWN;
    }

    boolean toNullIfUnknown(@Nullable Map.Entry<MergeCandidateIdentifier, T> input) {
        return (input != null ? input.getKey().getEntryType() : null) != EntryType.UNKNOWN;
    }
}
