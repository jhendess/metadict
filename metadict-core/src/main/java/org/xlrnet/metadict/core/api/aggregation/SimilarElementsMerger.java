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

package org.xlrnet.metadict.core.api.aggregation;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Merges similar elements of type {@code <T>} inside a collection together. The concrete implementation of the merger
 * defines what will be considered similar and how the elements will be processed.
 *
 * @param <T>
 *         the type of supported elements for merging
 */
public interface SimilarElementsMerger<T> {

    /**
     * Merges similar elements of type {@code <T>} inside the given collection together. It will be ensured by the
     * caller, that all elements inside the given collection are either of type {@code <T>} or a compatible subclass.
     * The returned collection must have either the same size or must be smaller than the input collection.
     *
     * @param collectionToMerge
     *         T Collection of elements to merge.
     * @return A collection of merged elements.
     */
    @NotNull
    Collection<T> merge(@NotNull Collection<T> collectionToMerge);
}
