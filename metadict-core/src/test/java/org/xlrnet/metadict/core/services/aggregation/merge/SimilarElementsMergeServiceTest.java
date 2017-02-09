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
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.xlrnet.metadict.core.api.aggregation.Merges;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

/**
 * Tests for {@link SimilarElementsMergeService}.
 */
public class SimilarElementsMergeServiceTest {

    public static final String MESSAGE = "__EVERYTHING_WORKS__";

    @Test
    public void testInitialization() throws Exception {
        Set<SimilarElementsMerger> mergers = new HashSet<>();
        mergers.add(new DefaultSimilarElementsMerger());
        SimilarElementsMergeService mergeService = new SimilarElementsMergeService(mergers);
        mergeService.initialize();  // May not throw an exception
    }

    @Test(expected = IllegalStateException.class)
    public void testInitialization_NoDefault() throws Exception {
        Set<SimilarElementsMerger> mergers = new HashSet<>();
        mergers.add(new LongSimilarElementsMerger());
        SimilarElementsMergeService mergeService = new SimilarElementsMergeService(mergers);
        mergeService.initialize();  // Must throw an exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNewMerger_duplicate() throws Exception {
        SimilarElementsMergeService mergeService = new SimilarElementsMergeService(Collections.emptySet());
        mergeService.registerNewMerger(new LongSimilarElementsMerger());
        mergeService.registerNewMerger(new LongSimilarElementsMerger()); // Must throw an exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNewMerger_noAnnotation() throws Exception {
        SimilarElementsMergeService mergeService = new SimilarElementsMergeService(Collections.emptySet());
        mergeService.registerNewMerger(new NoMerger());                   // Must throw an exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergeElements_multipleTypes() throws Exception {
        Set<SimilarElementsMerger> mergers = new HashSet<>();
        mergers.add(new DefaultSimilarElementsMerger());
        mergers.add(new LongSimilarElementsMerger());
        SimilarElementsMergeService mergeService = new SimilarElementsMergeService(mergers);
        mergeService.initialize();

        ImmutableList<Object> list = ImmutableList.of(1L, "String");
        mergeService.mergeElements(list, Long.class);
    }

    @Test
    public void mergeElements_fallback() throws Exception {
        DefaultSimilarElementsMerger defaultSimilarElementsMerger = spy(new DefaultSimilarElementsMerger());
        SimilarElementsMergeService mergeService = new SimilarElementsMergeService();
        mergeService.registerNewMerger(defaultSimilarElementsMerger);

        ImmutableList<Object> list = ImmutableList.of(1L, "String");
        mergeService.mergeElements(list, Object.class);
        verify(defaultSimilarElementsMerger).merge(list);
    }

    @Test
    public void mergeElements() throws Exception {
        DefaultSimilarElementsMerger defaultSimilarElementsMerger = spy(new DefaultSimilarElementsMerger());
        LongSimilarElementsMerger longSimilarElementsMerger = spy(new LongSimilarElementsMerger());

        SimilarElementsMergeService mergeService = new SimilarElementsMergeService();
        mergeService.registerNewMerger(defaultSimilarElementsMerger);
        mergeService.registerNewMerger(longSimilarElementsMerger);

        ImmutableList<Long> list = ImmutableList.of(1L);
        mergeService.mergeElements(list, Long.class);
        verify(defaultSimilarElementsMerger, never()).merge(any());
        verify(longSimilarElementsMerger).merge(list);
    }

    @Test
    public void mergeElements_subclass() throws Exception {
        DefaultSimilarElementsMerger defaultSimilarElementsMerger = spy(new DefaultSimilarElementsMerger());
        NumberSimilarElementsMerger numberSimilarElementsMerger = spy(new NumberSimilarElementsMerger());

        SimilarElementsMergeService mergeService = new SimilarElementsMergeService();
        mergeService.registerNewMerger(defaultSimilarElementsMerger);
        mergeService.registerNewMerger(numberSimilarElementsMerger);

        ImmutableList<Number> list = ImmutableList.of(1L);
        mergeService.mergeElements(list, Long.class);
        verify(defaultSimilarElementsMerger, never()).merge(any());
        verify(numberSimilarElementsMerger).merge(list);
    }

    /** Class with missing annotation. */
    private class NoMerger implements SimilarElementsMerger<Object> {

        @NotNull
        @Override
        public Collection<Object> merge(@NotNull Collection<Object> collectionToMerge) {
            return collectionToMerge;
        }
    }

    @Merges(Long.class)
    private class LongSimilarElementsMerger implements SimilarElementsMerger<Long> {

        @NotNull
        @Override
        public Collection<Long> merge(@NotNull Collection<Long> collectionToMerge) {
            return collectionToMerge;
        }
    }

    @Merges(Number.class)
    private class NumberSimilarElementsMerger implements SimilarElementsMerger<Number> {

        @NotNull
        @Override
        public Collection<Number> merge(@NotNull Collection<Number> collectionToMerge) {
            return collectionToMerge;
        }
    }
}