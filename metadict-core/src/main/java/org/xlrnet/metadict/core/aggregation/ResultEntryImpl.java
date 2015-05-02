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

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o
     *         the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException
     *         if the specified object is null
     * @throws ClassCastException
     *         if the specified object's type prevents it
     *         from being compared to this object.
     */
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

    /**
     * Get the entry's type. In most cases this is similar to a word class like nouns or verbs. However, you can also
     * provide phrases by using {@link EntryType#PHRASE}.
     *
     * @return the entry's type (i.e. word class in most cases).
     */
    @NotNull
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
    @NotNull
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
    @NotNull
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
