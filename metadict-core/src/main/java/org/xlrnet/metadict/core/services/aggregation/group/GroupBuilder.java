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

import org.xlrnet.metadict.core.api.aggregation.Group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for creating new {@link Group} objects.
 */
public class GroupBuilder<T> {

    private String groupIdentifier;

    private List<T> objectsInGroup = new ArrayList<>();

    /**
     * Adds all results from the given Iterable to this group.
     *
     * @param elements
     *         The elements that should be added.
     * @return The current builder.
     */
    public GroupBuilder addAll(Collection<T> elements) {
        checkNotNull(elements);

        this.objectsInGroup.addAll(elements);
        return this;
    }

    /**
     * Add a new single result entry to this group.
     *
     * @param element
     *         The element that should be added.
     * @return The current builder.
     */
    public GroupBuilder add(T element) {
        checkNotNull(element);

        this.objectsInGroup.add(element);
        return this;
    }

    /**
     * Returns a new {@link Group} instance. This will throw a {@link IllegalArgumentException} if the group
     * identifier is not set.
     *
     * @return
     */
    public Group<T> build() {
        checkArgument(this.objectsInGroup != null, "Group identifier may not be null");
        return new ImmutableGroup<>(this.groupIdentifier, this.objectsInGroup);
    }

    /**
     * Set a string representation of the group identifier. For example if the group is based on entry types the
     * identifier might be "NOUNS" for a group that contains only nouns.
     *
     * @param groupIdentifier
     *         a string representation of the group identifier.
     * @return The current builder.
     */
    public GroupBuilder<T> setGroupIdentifier(String groupIdentifier) {
        checkNotNull(groupIdentifier);

        this.groupIdentifier = groupIdentifier;
        return this;
    }
}
