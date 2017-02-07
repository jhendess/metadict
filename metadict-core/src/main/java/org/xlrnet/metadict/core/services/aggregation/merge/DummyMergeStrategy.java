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

import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.MonolingualEntry;
import org.xlrnet.metadict.core.api.aggregation.MergeStrategy;

import java.util.Collection;

/**
 * Very simple {@link MergeStrategy} which returns the exact same collection that was used as an input.
 */
public class DummyMergeStrategy implements MergeStrategy {

    @Override
    public Collection<BilingualEntry> mergeBilingualEntries(Collection<BilingualEntry> entriesToMerge) {
        return entriesToMerge;
    }

    @Override
    public Collection<MonolingualEntry> mergeMonolingualEntries(Collection<MonolingualEntry> entriesToMerge) {
        return entriesToMerge;
    }
}
