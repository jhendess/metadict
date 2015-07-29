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

package org.xlrnet.metadict.api.engine;

import com.google.common.base.MoreObjects;

import java.util.Iterator;

/**
 * Immutable implementation for {@link AutoTestSuite}.
 */
public class ImmutableAutoTestSuite implements AutoTestSuite{

    private final Iterable<AutoTestCase> autoTestCases;

    ImmutableAutoTestSuite(Iterable<AutoTestCase> autoTestCases) {
        this.autoTestCases = autoTestCases;
    }

    /**
     * Return a new builder instance for creating new {@link AutoTestSuite} objects.
     *
     * @return a new builder.
     */
    public static AutoTestSuiteBuilder builder() {
        return new AutoTestSuiteBuilder();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<AutoTestCase> iterator() {
        return autoTestCases.iterator();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("autoTestCases", autoTestCases)
                .toString();
    }
}
