/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
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

package org.xlrnet.metadict.impl.aggregation;

import org.jetbrains.annotations.NotNull;

/**
 * Use the {@link GroupingType} to determine how the {@link ResultEntry} should be grouped.
 */
public enum GroupingType {

    /**
     * The grouping will create only one group with all results in one group.
     */
    NONE(new NoneGroupingStrategy()),

    BY_ENGINE(null),

    BY_DICTIONARY(null),

    BY_ENTRYTYPE(new EntryTypeGroupingStrategy());

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
        return groupingStrategy;
    }
}
