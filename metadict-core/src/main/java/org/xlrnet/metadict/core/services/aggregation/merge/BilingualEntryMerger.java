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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.BilingualEntryBuilder;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.ImmutableBilingualEntry;
import org.xlrnet.metadict.core.api.aggregation.Merges;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;
import org.xlrnet.metadict.core.util.CommonUtils;

import java.util.*;

/**
 * <p>Implementation of {@link SimilarElementsMerger} for {@link BilingualEntry} objects.
 */
@Merges(BilingualEntry.class)
public class BilingualEntryMerger extends AbstractEntryMerger<BilingualEntry> {

    /**
     * Function which is used  for indexing {@link BilingualEntry} objects into a map.
     */
    @Override
    @NotNull
    protected MergeCandidateIdentifier buildCandidateIdentifier(@NotNull BilingualEntry input) {
        ImmutablePair<Language, Language> languagePair = ImmutablePair.of(input.getSource().getLanguage(), input.getTarget().getLanguage());
        ImmutablePair<String, String> generalFormPair = ImmutablePair.of(CommonUtils.simpleNormalize(input.getSource().getGeneralForm()), CommonUtils.simpleNormalize(input.getTarget().getGeneralForm()));
        return new MergeCandidateIdentifier(languagePair, input.getEntryType(), generalFormPair);
    }

    @Override
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

        DictionaryObject sourceObject = DictionaryObjectMergeUtil.mergeDictionaryObjects(sourceObjects);
        DictionaryObject targetObject = DictionaryObjectMergeUtil.mergeDictionaryObjects(targetObjects);

        builder.setInputObject(sourceObject).setOutputObject(targetObject);
        return builder.build();
    }

    @Override
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
}
