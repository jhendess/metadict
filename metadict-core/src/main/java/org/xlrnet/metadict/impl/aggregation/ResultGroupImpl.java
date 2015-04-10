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

import com.google.common.base.MoreObjects;

import java.util.Collection;
import java.util.Iterator;

/**
 * Implementation of {@link ResultGroup}.
 */
public class ResultGroupImpl implements ResultGroup {

    private final String groupIdentifier;

    private final Collection<ResultEntry> resultEntries;

    ResultGroupImpl(String groupIdentifier, Collection<ResultEntry> resultEntries) {
        this.groupIdentifier = groupIdentifier;
        this.resultEntries = resultEntries;
    }

    /**
     * Returns a string representation of the group identifier. For example if the group is based on entry types the
     * identifier might be "NOUNS" for a group that contains only nouns.
     *
     * @return a string representation of the group identifier.
     */
    @Override
    public String getGroupIdentifier() {
        return groupIdentifier;
    }

    /**
     * Returns the entries of this group.
     *
     * @return the entries of this group.
     */
    @Override
    public Collection<ResultEntry> getResultEntries() {
        return resultEntries;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<ResultEntry> iterator() {
        return resultEntries.iterator();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupIdentifier", groupIdentifier)
                .add("resultEntries", resultEntries)
                .toString();
    }
}
