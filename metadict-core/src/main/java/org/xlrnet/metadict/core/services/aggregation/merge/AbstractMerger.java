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

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract {@link SimilarElementsMerger} which provides a simple three-step algorithm:
 * <ul>
 * <li>Normalize all input objects</li>
 * <li>Find possible candidates</li>
 * <li>Merge the candidates</li>
 * </ul>>
 */
public abstract class AbstractMerger<T> implements SimilarElementsMerger<T> {

    @NotNull
    @Override
    public Collection<T> merge(@NotNull Collection<T> collectionToMerge) {
        Collection<T> normalizedInput = normalizeInput(collectionToMerge);
        Collection<Collection<T>> candidates = findCandidates(normalizedInput);
        return mergeCandidates(candidates);
    }

    /**
     * Normalizes the input collection and makes sure that all entries from the same dictionary are ordered the same.
     */
    @NotNull
    abstract Collection<T> normalizeInput(@NotNull Collection<T> collectionToMerge);

    /**
     * Find potential candidates for merging by grouping elements with the same dictionary, entry type and general form.
     */
    @NotNull
    abstract protected Collection<Collection<T>> findCandidates(@NotNull Collection<T> normalizedInput);

    /**
     * Merges a collection of objects to a single object.
     *
     * @param candidate
     *         The candidates for merging.
     * @return a single merged objects.
     */
    @NotNull
    abstract protected T mergeCandidate(@NotNull Collection<T> candidate);

    @NotNull
    protected Collection<T> mergeCandidates(@NotNull Collection<Collection<T>> candidates) {
        Collection<T> merged = new ArrayList<>(candidates.size());
        for (Collection<T> candidate : candidates) {
            merged.add(mergeCandidate(candidate));
        }
        return merged;
    }
}
