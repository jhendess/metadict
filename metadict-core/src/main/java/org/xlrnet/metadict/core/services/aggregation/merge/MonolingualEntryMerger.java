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
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.api.aggregation.Merges;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;
import org.xlrnet.metadict.core.util.CommonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Concrete implementation of {@link SimilarElementsMerger} for {@link MonolingualEntry} objects.
 */
@Merges(MonolingualEntry.class)
public class MonolingualEntryMerger extends AbstractEntryMerger<MonolingualEntry> {

    @NotNull
    @Override
    protected MergeCandidateIdentifier buildCandidateIdentifier(@NotNull MonolingualEntry input) {
        ImmutablePair<Language, Language> languagePair = ImmutablePair.of(input.getContent().getLanguage(), null);
        ImmutablePair<String, String> generalFormPair = ImmutablePair.of(CommonUtils.simpleNormalize(input.getContent().getGeneralForm()), null);
        return new MergeCandidateIdentifier(languagePair, input.getEntryType(), generalFormPair);
    }

    @Override
    protected MonolingualEntry enrichEntryByEntryType(MonolingualEntry entry, EntryType entryType) {
        return ImmutableMonolingualEntry.builder().setContent(entry.getContent()).setEntryType(entryType).build();
    }

    @NotNull
    @Override
    Collection<MonolingualEntry> normalizeInput(@NotNull Collection<MonolingualEntry> collectionToMerge) {
        // No special normalization necessary
        return collectionToMerge;
    }

    @NotNull
    @Override
    protected MonolingualEntry mergeCandidate(@NotNull Collection<MonolingualEntry> candidates) {
        MonolingualEntryBuilder builder = ImmutableMonolingualEntry.builder();
        List<DictionaryObject> dictionaryObjects = new ArrayList<>(candidates.size());

        boolean isFirst = true;
        for (MonolingualEntry candidate : candidates) {
            if (isFirst) {
                builder.setEntryType(candidate.getEntryType());
                isFirst = false;
            }
            dictionaryObjects.add(candidate.getContent());
        }

        DictionaryObject mergedObject = DictionaryObjectMergeUtil.mergeDictionaryObjects(dictionaryObjects);

        builder.setContent(mergedObject);
        return builder.build();
    }
}
