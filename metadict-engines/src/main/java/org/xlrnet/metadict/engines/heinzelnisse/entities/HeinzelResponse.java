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
import com.google.common.base.Objects;

import java.util.List;

/**
 * The class {@see HeinzelResponse} serves as the main container for responses from the Heinzelnisse backend. It is
 * meant to be used with Jackson Mapping for simpler processing.
 */
public class HeinzelResponse {

    @JsonProperty("forumQuestions")
    private List<ForumQuestion> forumQuestions;

    @JsonProperty("searchItem")
    private String searchItem;

    @JsonProperty("noPhonetics")
    private List<String> norwegianPhonetics;

    @JsonProperty("nynorskWords")
    private List<String> nynorskWords;

    @JsonProperty("bookmaalWords")
    private List<String> bookmaalWords;

    @JsonProperty("noTrans")
    private List<TranslationEntry> norwegianTranslations;

    @JsonProperty("deTrans")
    private List<TranslationEntry> germanTranslations;

    @JsonProperty("dePhonetics")
    private List<String> germanPhonetics;

    @JsonProperty("wikiPageNames")
    private List<String> wikiPageNames;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeinzelResponse)) return false;
        HeinzelResponse that = (HeinzelResponse) o;
        return Objects.equal(forumQuestions, that.forumQuestions) &&
                Objects.equal(searchItem, that.searchItem) &&
                Objects.equal(norwegianPhonetics, that.norwegianPhonetics) &&
                Objects.equal(nynorskWords, that.nynorskWords) &&
                Objects.equal(bookmaalWords, that.bookmaalWords) &&
                Objects.equal(norwegianTranslations, that.norwegianTranslations) &&
                Objects.equal(germanTranslations, that.germanTranslations) &&
                Objects.equal(germanPhonetics, that.germanPhonetics) &&
                Objects.equal(wikiPageNames, that.wikiPageNames);
    }

    public List<String> getBookmaalWords() {
        return bookmaalWords;
    }

    public List<ForumQuestion> getForumQuestions() {
        return forumQuestions;
    }

    public List<String> getGermanPhonetics() {
        return germanPhonetics;
    }

    public List<TranslationEntry> getGermanTranslations() {
        return germanTranslations;
    }

    public List<String> getNorwegianPhonetics() {
        return norwegianPhonetics;
    }

    public List<TranslationEntry> getNorwegianTranslations() {
        return norwegianTranslations;
    }

    public List<String> getNynorskWords() {
        return nynorskWords;
    }

    public String getSearchItem() {
        return searchItem;
    }

    public List<String> getWikiPageNames() {
        return wikiPageNames;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(forumQuestions, searchItem, norwegianPhonetics, nynorskWords, bookmaalWords, norwegianTranslations, germanTranslations, germanPhonetics, wikiPageNames);
    }

    @Override
    public String toString() {
        return "HeinzelResponse{" +
                "forumQuestions=" + forumQuestions +
                ", searchItem='" + searchItem + '\'' +
                ", norwegianPhonetics=" + norwegianPhonetics +
                ", nynorskWords=" + nynorskWords +
                ", bookmaalWords=" + bookmaalWords +
                ", norwegianTranslations=" + norwegianTranslations +
                ", germanTranslations=" + germanTranslations +
                ", germanPhonetics=" + germanPhonetics +
                ", wikiPageNames=" + wikiPageNames +
                '}';
    }
}
