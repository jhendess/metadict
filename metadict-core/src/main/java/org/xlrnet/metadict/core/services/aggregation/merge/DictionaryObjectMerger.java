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

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.core.api.aggregation.Merges;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;
import org.xlrnet.metadict.core.util.CommonUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Concrete implementation of {@link SimilarElementsMerger} for {@link
 * DictionaryObject}.
 */
@Merges(DictionaryObject.class)
public class DictionaryObjectMerger extends AbstractMerger<DictionaryObject> {

    @NotNull
    @Override
    Collection<DictionaryObject> normalizeInput(@NotNull Collection<DictionaryObject> collectionToMerge) {
        // No normalization necessary
        return collectionToMerge;
    }

    @NotNull
    @Override
    protected Collection<Collection<DictionaryObject>> findCandidates(@NotNull Collection<DictionaryObject> normalizedInput) {
        Multimap<Pair<String, Language>, DictionaryObject> candidateMap = MultimapBuilder.hashKeys().linkedListValues().build();

        for (DictionaryObject dictionaryObject : normalizedInput) {
            Pair<String, Language> candidateIdentifier = ImmutablePair.of(
                    CommonUtils.simpleNormalize(dictionaryObject.getGeneralForm()),
                    dictionaryObject.getLanguage()
            );
            candidateMap.put(candidateIdentifier, dictionaryObject);
        }

        Collection<Collection<DictionaryObject>> candidates = new ArrayList<>(candidateMap.keySet().size());
        for (Pair<String, Language> key : candidateMap.keySet()) {
            candidates.add(candidateMap.get(key));
        }

        return candidates;
    }

    @NotNull
    @Override
    protected DictionaryObject mergeCandidate(@NotNull Collection<DictionaryObject> candidate) {
        return DictionaryObjectMergeUtil.mergeDictionaryObjects(Lists.newArrayList(candidate));
    }
}
