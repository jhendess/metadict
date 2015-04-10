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

/**
 * Created by xolor on 07.04.15.
 */
public class TranslationEntry {

    @JsonProperty("t_other")
    private String translatedOtherInformation;

    @JsonProperty("id")
    private int id;

    /**
     * Indication for either the word type or grammatical gender (if noun).
     * <p>
     * Example:
     * m -> male noun
     * f -> female noun
     * n -> neutral noun
     * m/f -> male/female noun
     * v -> verb
     * adj -> adjective
     * adv -> adverb
     * prep -> preposition
     * pron -> pronoun
     */
    @JsonProperty("t_article")
    private String translatedArticle;

    @JsonProperty("category")
    private String category;

    @JsonProperty("t_word")
    private String translatedWord;

    @JsonProperty("other")
    private String otherInformation;

    @JsonProperty("article")
    private String article;

    @JsonProperty("bokmaalLink")
    private String bokmaalLink;

    @JsonProperty("word")
    private String word;

    @JsonProperty("canooLink")
    private String canooLink;

    @JsonProperty("grade")
    private String grade;

    @JsonProperty("lang")
    private Object lang;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranslationEntry)) return false;
        TranslationEntry that = (TranslationEntry) o;
        return Objects.equal(id, that.id) &&
                Objects.equal(translatedOtherInformation, that.translatedOtherInformation) &&
                Objects.equal(translatedArticle, that.translatedArticle) &&
                Objects.equal(category, that.category) &&
                Objects.equal(translatedWord, that.translatedWord) &&
                Objects.equal(otherInformation, that.otherInformation) &&
                Objects.equal(article, that.article) &&
                Objects.equal(bokmaalLink, that.bokmaalLink) &&
                Objects.equal(word, that.word) &&
                Objects.equal(canooLink, that.canooLink) &&
                Objects.equal(grade, that.grade) &&
                Objects.equal(lang, that.lang);
    }

    public String getArticle() {
        return article;
    }

    public String getBokmaalLink() {
        return bokmaalLink;
    }

    public String getCanooLink() {
        return canooLink;
    }

    public String getCategory() {
        return category;
    }

    public String getGrade() {
        return grade;
    }

    public int getId() {
        return id;
    }

    public Object getLang() {
        return lang;
    }

    public String getOtherInformation() {
        return otherInformation;
    }

    public String getTranslatedArticle() {
        return translatedArticle;
    }

    public String getTranslatedOtherInformation() {
        return translatedOtherInformation;
    }

    public String getTranslatedWord() {
        return translatedWord;
    }

    public String getWord() {
        return word;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(translatedOtherInformation, id, translatedArticle, category, translatedWord, otherInformation, article, bokmaalLink, word, canooLink, grade, lang);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("translatedOtherInformation", translatedOtherInformation)
                .add("id", id)
                .add("translatedArticle", translatedArticle)
                .add("category", category)
                .add("translatedWord", translatedWord)
                .add("otherInformation", otherInformation)
                .add("article", article)
                .add("bokmaalLink", bokmaalLink)
                .add("word", word)
                .add("canooLink", canooLink)
                .add("grade", grade)
                .add("lang", lang)
                .toString();
    }

}
