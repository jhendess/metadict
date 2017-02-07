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

import com.google.common.base.MoreObjects;
import org.xlrnet.metadict.core.api.aggregation.Group;

import java.util.Iterator;
import java.util.List;

/**
 * Immutable implementation of {@link Group}.
 */
public class ImmutableGroup<T> implements Group<T> {

    private static final long serialVersionUID = -3599681937843767303L;

    private final String groupIdentifier;

    private final List<T> resultEntries;

    ImmutableGroup(String groupIdentifier, List<T> elements) {
        this.groupIdentifier = groupIdentifier;
        this.resultEntries = elements;
    }

    /**
     * Returns a string representation of the group identifier. For example if the group is based on entry types the
     * identifier might be "NOUNS" for a group that contains only nouns.
     *
     * @return a string representation of the group identifier.
     */
    @Override
    public String getGroupIdentifier() {
        return this.groupIdentifier;
    }

    /**
     * Returns the entries of this group.
     *
     * @return the entries of this group.
     */
    @Override
    public List<T> getResultEntries() {
        return this.resultEntries;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<T> iterator() {
        return this.resultEntries.iterator();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupIdentifier", this.groupIdentifier)
                .add("elements", this.resultEntries)
                .toString();
    }
}
