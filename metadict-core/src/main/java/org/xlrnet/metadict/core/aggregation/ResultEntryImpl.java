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

package org.xlrnet.metadict.core.aggregation;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.EntryType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation for {@link ResultEntry}. This class uses a decorator to wrap an existing {@link
 * org.xlrnet.metadict.api.query.BilingualEntry}.
 */
public class ResultEntryImpl implements ResultEntry {

    private final BilingualEntry dictionaryEntry;

    private final String sourceEngine;

    private double entryScore;

    private ResultEntryImpl(BilingualEntry dictionaryEntry, String sourceEngine, double entryScore) {
        checkNotNull(dictionaryEntry, "Wrapped BilingualEntry may not be null");
        checkNotNull(sourceEngine, "Engine name may not be null");
        checkArgument(entryScore >= 0.0 && entryScore <= 1.0, "Entry score must be in range [0.0;1.0]");

        this.dictionaryEntry = dictionaryEntry;
        this.sourceEngine = sourceEngine;
        this.entryScore = entryScore;
    }

    /**
     * Wrap a new {@link ResultEntry} around an existing {@link BilingualEntry}.
     *
     * @param dictionaryEntry
     *         The {@link BilingualEntry} that should be wrapped.
     * @param sourceEngine
     *         The name of the engine that provided this result entry.
     * @param entryScore
     *         The relevance score for this entry. Must be between 0.0 and 1.0 (inclusive).
     * @return a new {@link ResultEntry} object around the given {@link BilingualEntry}.
     */
    public static ResultEntry from(BilingualEntry dictionaryEntry, String sourceEngine, double entryScore) {
        return new ResultEntryImpl(dictionaryEntry, sourceEngine, entryScore);
    }

    /**
     * Wrap a new {@link ResultEntry} around an existing {@link BilingualEntry}. The entry score of this entry will
     * have the value 1.0.
     *
     * @param dictionaryEntry
     *         The {@link BilingualEntry} that should be wrapped.
     * @param sourceEngine
     *         The name of the engine that provided this result entry.
     * @return a new {@link ResultEntry} object around the given {@link BilingualEntry}.
     */
    public static ResultEntry from(BilingualEntry dictionaryEntry, String sourceEngine) {
        return new ResultEntryImpl(dictionaryEntry, sourceEngine, 1.0);
    }

    @Override
    public int compareTo(ResultEntry o) {
        return Double.compare(o.getEntryScore(), this.entryScore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultEntryImpl)) return false;
        ResultEntryImpl that = (ResultEntryImpl) o;
        return Objects.equal(entryScore, that.entryScore) &&
                Objects.equal(dictionaryEntry, that.dictionaryEntry) &&
                Objects.equal(sourceEngine, that.sourceEngine);
    }

    /**
     * Returns the calculated relevance score for this entry. The score should be a value between 0.0 and 1.0
     * (inclusive) where a value of 1.0 means best possible relevancy.
     *
     * @return the calculated relevance score for this entry.
     */
    @Override
    public double getEntryScore() {
        return this.entryScore;
    }

    /**
     * Overwrite the current relevance score.
     *
     * @param entryScore
     *         The relevance score for this entry. Must be between 0.0 and 1.0 (inclusive).
     * @return
     */
    public ResultEntryImpl setEntryScore(double entryScore) {
        checkArgument(entryScore >= 0.0 && entryScore <= 1.0, "Entry score must be in range [0.0;1.0]");

        this.entryScore = entryScore;
        return this;
    }

    @NotNull
    @Override
    public EntryType getEntryType() {
        return dictionaryEntry.getEntryType();
    }

    @NotNull
    @Override
    public DictionaryObject getSource() {
        return dictionaryEntry.getSource();
    }

    @NotNull
    @Override
    public DictionaryObject getTarget() {
        return dictionaryEntry.getTarget();
    }

    @Override
    public String getSourceEngine() {
        return sourceEngine;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dictionaryEntry, sourceEngine, entryScore);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dictionaryEntry", dictionaryEntry)
                .add("sourceEngine", sourceEngine)
                .add("entryScore", entryScore)
                .toString();
    }
}
