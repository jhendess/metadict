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

package org.xlrnet.metadict.core.query;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Language;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Query step for executing a Bilingual query.
 */
public class BilingualQueryStep extends AbstractQueryStep {

    private Language inputLanguage;

    private Language outputLanguage;

    private boolean allowBothWay;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BilingualQueryStep)) return false;
        BilingualQueryStep queryStep = (BilingualQueryStep) o;
        return Objects.equal(allowBothWay, queryStep.allowBothWay) &&
                Objects.equal(searchEngineName, queryStep.searchEngineName) &&
                Objects.equal(queryString, queryStep.queryString) &&
                Objects.equal(searchEngine, queryStep.searchEngine) &&
                Objects.equal(inputLanguage, queryStep.inputLanguage) &&
                Objects.equal(outputLanguage, queryStep.outputLanguage);
    }

    @NotNull
    public Language getInputLanguage() {
        return inputLanguage;
    }

    @NotNull
    public BilingualQueryStep setInputLanguage(Language inputLanguage) {
        checkNotNull(inputLanguage);

        this.inputLanguage = inputLanguage;
        return this;
    }

    @NotNull
    public Language getOutputLanguage() {
        return outputLanguage;
    }

    @NotNull
    public BilingualQueryStep setOutputLanguage(@NotNull Language outputLanguage) {
        checkNotNull(outputLanguage);

        this.outputLanguage = outputLanguage;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(searchEngineName, queryString, searchEngine, inputLanguage, outputLanguage, allowBothWay);
    }

    public boolean isAllowBothWay() {
        return allowBothWay;
    }

    @NotNull
    public BilingualQueryStep setAllowBothWay(boolean allowBothWay) {
        this.allowBothWay = allowBothWay;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("searchEngineName", searchEngineName)
                .add("queryString", queryString)
                .add("inputLanguage", inputLanguage)
                .add("outputLanguage", outputLanguage)
                .add("allowBothWay", allowBothWay)
                .toString();
    }
}
