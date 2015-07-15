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

package org.xlrnet.metadict.engines.heinzelnisse.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;

/**
 * A single forum thread on Heinzelnisse.
 */
public class ForumQuestion {

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("id")
    private int id;

    @JsonProperty("keywordString")
    private String keywordString;

    @JsonProperty("answers")
    private List<ForumAnswer> answers;

    @JsonProperty("html")
    private String html;

    @JsonProperty("userName")

    private String userName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForumQuestion)) return false;
        ForumQuestion that = (ForumQuestion) o;
        return Objects.equal(id, that.id) &&
                Objects.equal(timestamp, that.timestamp) &&
                Objects.equal(keywordString, that.keywordString) &&
                Objects.equal(answers, that.answers) &&
                Objects.equal(html, that.html) &&
                Objects.equal(userName, that.userName);
    }

    public List<ForumAnswer> getAnswers() {
        return answers;
    }

    public String getHtml() {
        return html;
    }

    public int getId() {
        return id;
    }

    public String getKeywordString() {
        return keywordString;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserName() {

        return userName;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timestamp, id, keywordString, answers, html, userName);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp", timestamp)
                .add("id", id)
                .add("keywordString", keywordString)
                .add("answers", answers)
                .add("html", html)
                .add("userName", userName)
                .toString();
    }
}
