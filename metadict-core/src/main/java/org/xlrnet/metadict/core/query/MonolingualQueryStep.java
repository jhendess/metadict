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
import org.xlrnet.metadict.api.language.Language;

import java.io.Serializable;

/**
 * Query step for executing a Monolingual query.
 */
public class MonolingualQueryStep extends AbstractQueryStep implements Serializable {

    private static final long serialVersionUID = -9136988588523162829L;

    private Language requestLanguage;

    public Language getRequestLanguage() {
        return requestLanguage;
    }

    public MonolingualQueryStep setRequestLanguage(Language requestLanguage) {
        this.requestLanguage = requestLanguage;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(searchEngineName, queryString, searchEngine, requestLanguage);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("searchEngineName", searchEngineName)
                .add("queryString", queryString)
                .add("requestLanguage", requestLanguage)
                .toString();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof MonolingualQueryStep)) return false;
        MonolingualQueryStep queryStep = (MonolingualQueryStep) o;
        return Objects.equal(requestLanguage, queryStep.requestLanguage) &&
                Objects.equal(searchEngineName, queryStep.searchEngineName) &&
                Objects.equal(queryString, queryStep.queryString) &&
                Objects.equal(searchEngine, queryStep.searchEngine);
    }
}
