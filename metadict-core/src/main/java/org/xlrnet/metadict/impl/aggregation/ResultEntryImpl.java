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
import com.google.common.base.Objects;
import org.xlrnet.metadict.api.query.DictionaryEntry;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.EntryType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation for {@link ResultEntry}. This class uses a decorator to wrap an existing {@link
 * org.xlrnet.metadict.api.query.DictionaryEntry}.
 */
public class ResultEntryImpl implements ResultEntry {

    private final DictionaryEntry dictionaryEntry;

    private final String sourceEngine;

    private final double entryScore;

    public ResultEntryImpl(DictionaryEntry dictionaryEntry, String sourceEngine, double entryScore) {
        checkNotNull(dictionaryEntry, "Wrapped DictionaryEntry may not be null");
        checkNotNull(sourceEngine, "Engine name may not be null");
        checkArgument(entryScore >= 0.0 && entryScore <= 1.0, "Entry score must be in range [0.0;1.0]");

        this.dictionaryEntry = dictionaryEntry;
        this.sourceEngine = sourceEngine;
        this.entryScore = entryScore;
    }

    /**
     * Wrap a new {@link ResultEntry} around an existing {@link DictionaryEntry}.
     *
     * @param dictionaryEntry
     *         The {@link DictionaryEntry} that should be wrapped.
     * @param sourceEngine
     *         The name of the engine that provided this result entry.
     * @param entryScore
     *         The relevance score for this entry. Must be between 0.0 and 1.0 (inclusive).
     * @return a new {@link ResultEntry} object around the given {@link DictionaryEntry}.
     */
    public static ResultEntry from(DictionaryEntry dictionaryEntry, String sourceEngine, double entryScore) {
        return new ResultEntryImpl(dictionaryEntry, sourceEngine, entryScore);
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
     * Get the entry's type. In most cases this is similar to a word class like nouns or verbs. However, you can also
     * provide phrases by using {@link EntryType#PHRASE}.
     *
     * @return the entry's type (i.e. word class in most cases).
     */
    @Override
    public EntryType getEntryType() {
        return dictionaryEntry.getEntryType();
    }

    /**
     * Get the entry's {@link DictionaryObject} that contains information in the input language. This object doesn't
     * have to correspond exactly to the original input query, but be should as similar as possible. If the entry
     * originates from a one-language dictionary, this method has to return the word's meaning. The value may never be
     * null.
     *
     * @return the {@link DictionaryObject} in input language.
     */
    @Override
    public DictionaryObject getInput() {
        return dictionaryEntry.getInput();
    }

    /**
     * Get the entry's {@link DictionaryObject} that contains information in the output language. If the entry
     * originates from a one-language dictionary, the returned value may be null.
     *
     * @return the {@link DictionaryObject} in output language or null for one-language dictionaries.
     */
    @Override
    public DictionaryObject getOutput() {
        return dictionaryEntry.getOutput();
    }

    /**
     * Returns the name of the engine that produced this entry.
     *
     * @return the name of the engine that produced this entry.
     */
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
