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

package org.xlrnet.metadict.core.services.aggregation.group;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.core.api.aggregation.GroupingStrategy;
import org.xlrnet.metadict.core.api.aggregation.ResultEntry;

/**
 * Use the {@link GroupingType} to determine how the {@link ResultEntry} should be grouped.
 */
public enum GroupingType {

    /** Only one group with all results. */
    NONE(new NoneGroupingStrategy()),

    /** Group results based on the source engine. */
    ENGINE(null),

    /** Group results based on the used dictionary. */
    DICTIONARY(new DictionaryGroupingStrategy()),

    /** Group results based on the entry type of each result */
    ENTRYTYPE(new EntryTypeGroupingStrategy());

    private GroupingStrategy groupingStrategy;

    GroupingType(GroupingStrategy groupingStrategy) {
        this.groupingStrategy = groupingStrategy;
    }

    /**
     * Returns the strategy that should be used for this type of grouping.
     *
     * @return the strategy that should be used for this type of grouping.
     */
    @NotNull
    public GroupingStrategy getGroupingStrategy() {
        return this.groupingStrategy;
    }
}
