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
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.query.BilingualEntry;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.EntryType;
import org.xlrnet.metadict.core.api.aggregation.ResultEntry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation for {@link ResultEntry}. This class uses a decorator to wrap an existing {@link
 * org.xlrnet.metadict.api.query.BilingualEntry}.
 */
public class ScoredResultEntry implements ResultEntry {

    private static final long serialVersionUID = 8973250201767143107L;

    private final BilingualEntry dictionaryEntry;

    private double entryScore;

    private ScoredResultEntry(BilingualEntry dictionaryEntry, double entryScore) {
        checkNotNull(dictionaryEntry, "Wrapped BilingualEntry may not be null");
        checkArgument(entryScore >= 0.0 && entryScore <= 1.0, "Entry score must be in range [0.0;1.0]");

        this.dictionaryEntry = dictionaryEntry;
        this.entryScore = entryScore;
    }

    /**
     * Wrap a new {@link ResultEntry} around an existing {@link BilingualEntry}.
     *
     * @param dictionaryEntry
     *         The {@link BilingualEntry} that should be wrapped.
     * @param entryScore
     *         The relevance score for this entry. Must be between 0.0 and 1.0 (inclusive).
     * @return a new {@link ResultEntry} object around the given {@link BilingualEntry}.
     */
    public static ResultEntry from(BilingualEntry dictionaryEntry, double entryScore) {
        return new ScoredResultEntry(dictionaryEntry, entryScore);
    }

    @Override
    public int compareTo(ResultEntry o) {
        return Double.compare(o.getEntryScore(), this.entryScore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScoredResultEntry)) return false;
        ScoredResultEntry that = (ScoredResultEntry) o;
        return Objects.equal(this.entryScore, that.entryScore) &&
                Objects.equal(this.dictionaryEntry, that.dictionaryEntry);
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
    public ScoredResultEntry setEntryScore(double entryScore) {
        checkArgument(entryScore >= 0.0 && entryScore <= 1.0, "Entry score must be in range [0.0;1.0]");

        this.entryScore = entryScore;
        return this;
    }

    @NotNull
    @Override
    public EntryType getEntryType() {
        return this.dictionaryEntry.getEntryType();
    }

    @NotNull
    @Override
    public DictionaryObject getSource() {
        return this.dictionaryEntry.getSource();
    }

    @NotNull
    @Override
    public DictionaryObject getTarget() {
        return this.dictionaryEntry.getTarget();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.dictionaryEntry, this.entryScore);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dictionaryEntry", this.dictionaryEntry)
                .add("entryScore", this.entryScore)
                .toString();
    }
}
