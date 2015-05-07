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

package org.xlrnet.metadict.engines.nobordbok;

import com.google.common.collect.ImmutableMap;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.language.UnsupportedLanguageException;
import org.xlrnet.metadict.api.query.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Engine for nob-ordbok.no backend.
 */
public class OrdbokEngine implements SearchEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdbokEngine.class);

    @NotNull
    @Override
    public MonolingualQueryResult executeMonolingualQuery(@NotNull String queryString, @NotNull Language queryLanguage) throws Exception {
        if (!(Language.NORWEGIAN_BOKMÅL.equals(queryLanguage) || Language.NORWEGIAN_NYNORSK.equals(queryLanguage) || Language.NORWEGIAN.equals(queryLanguage))) {
            throw new UnsupportedLanguageException(queryLanguage);
        }

        Document document = fetchResponse(queryString, queryLanguage);

        return processDocument(document);
    }

    @NotNull
    private MonolingualQueryResult processDocument(@NotNull Document document) {
        MonolingualQueryResultBuilder resultBuilder = new MonolingualQueryResultBuilder();

        Element bokmaalTable = document.getElementById("byttutBM");
        Element nynorskTable = document.getElementById("byttutNN");

        if (bokmaalTable != null)
            processTable(bokmaalTable, Language.NORWEGIAN_BOKMÅL, resultBuilder);
        if (nynorskTable != null)
            processTable(nynorskTable, Language.NORWEGIAN_NYNORSK, resultBuilder);

        return resultBuilder.build();
    }

    private void processTable(@NotNull Element table, @NotNull Language language, @NotNull MonolingualQueryResultBuilder resultBuilder) {
        Elements tableRows = table.getElementsByTag("tr");

        if (tableRows.size() <= 1) {
            LOGGER.warn("Word table has unexpected size {}", tableRows.size());
            return;
        }

        for (int i = 1; i < tableRows.size(); i++) {
            Element tableRow = tableRows.get(i);
            MonolingualEntry entry = processTableRow(tableRow, language);
            resultBuilder.addMonolingualEntry(entry);
        }
    }

    @NotNull
    private MonolingualEntry processTableRow(@NotNull Element tableRow, @NotNull Language language) {
        MonolingualEntryBuilder entryBuilder = new MonolingualEntryBuilder();
        DictionaryObjectBuilder objectBuilder = new DictionaryObjectBuilder().setLanguage(language);

        // Extract general form
        String generalForm = tableRow.getElementsByClass("oppslagsord").first().text();
        objectBuilder.setGeneralForm(generalForm);

        // Extract wordclass and determine entrytype
        String wordClass = tableRow.getElementsByClass("oppsgramordklasse").first().text();
        entryBuilder.setEntryType(resolveEntryTypeWithWordClass(wordClass));

        // Get meanings
        tableRow.getElementsByClass("tyding").forEach(
                (element -> objectBuilder.addMeaning(element.text()))
        );

        entryBuilder.setContent(objectBuilder.build());

        return entryBuilder.build();
    }

    private static final Map<String, EntryType> ENTRY_TYPE_MAP = ImmutableMap.<String, EntryType>builder()
            .put("verb", EntryType.VERB)
            .put("adv.", EntryType.ADVERB)
            .put("adj.", EntryType.PREPOSITION)
            .put("prep.", EntryType.PREPOSITION)
            .put("konj.", EntryType.CONJUNCTION)
            .build();

    /**
     * Try to resolve the {@link EntryType} with a given "word class" string from the bokmaalordboka.
     * <p/>
     * Supported entries:
     * <ul>
     * <li>mX -> male noun (X is any int)</li>
     * <li>fX -> female noun (X is any int</li>
     * <li>nX -> neuter noun (X is any int)</li>
     * <li>adv. -> adverb</li>>
     * <li>adj. -> adjective</li>
     * <li>aX -> adjective (X is any int)</li>
     * <li>prep. -> preposition</li>
     * <li>konj. -> conjuction</li>
     * <li>vX -> verb (X is any int)</li>
     * <li>verb -> verb</li>
     * </ul>
     *
     * @param wordClass
     *         The word class.
     * @return a valid metadict entry type.
     */
    @NotNull
    private EntryType resolveEntryTypeWithWordClass(@NotNull String wordClass) {
        EntryType entryType = EntryType.UNKNOWN;

        if (ENTRY_TYPE_MAP.containsKey(wordClass))
            entryType = ENTRY_TYPE_MAP.get(wordClass);
        else if (StringUtils.startsWithIgnoreCase(wordClass, "a"))
            entryType = EntryType.ADJECTIVE;
        else if (StringUtils.startsWithIgnoreCase(wordClass, "m"))
            entryType = EntryType.NOUN;
        else if (StringUtils.startsWithIgnoreCase(wordClass, "f"))
            entryType = EntryType.NOUN;
        else if (StringUtils.startsWithIgnoreCase(wordClass, "n"))
            entryType = EntryType.NOUN;
        else if (StringUtils.startsWithIgnoreCase(wordClass, "v"))
            entryType = EntryType.VERB;

        return entryType;
    }

    @NotNull
    private String buildTargetUrl(@NotNull String searchRequest, boolean queryBokmaal, boolean queryNynorsk) throws UnsupportedEncodingException {
        StringBuilder targetUrlBuilder = new StringBuilder("http://www.nob-ordbok.uio.no/perl/ordbok.cgi?OPP=")
                .append(URLEncoder.encode(searchRequest, "UTF-8"))
                .append("&");

        if (queryBokmaal & queryNynorsk) {
            targetUrlBuilder.append("begge=+&ordbok=begge");
        } else if (queryBokmaal) {
            targetUrlBuilder.append("bokmaal=+&ordbok=bokmaal");
        } else if (queryNynorsk) {
            targetUrlBuilder.append("nynorsk=+&ordbok=nynorsk");
        } else {
            throw new IllegalArgumentException("Either nynorsk or bokmaal must be queried");
        }

        return targetUrlBuilder.toString();
    }

    private Document fetchResponse(@NotNull String queryString, @NotNull Language queryLanguage) throws IOException {
        boolean queryBokmaal = false, queryNynorsk = false;

        if (queryLanguage.equals(Language.NORWEGIAN_BOKMÅL) || queryLanguage.equals(Language.NORWEGIAN))
            queryBokmaal = true;
        if (queryLanguage.equals(Language.NORWEGIAN_NYNORSK) || queryLanguage.equals(Language.NORWEGIAN))
            queryNynorsk = true;

        String targetUrl = buildTargetUrl(queryString, queryBokmaal, queryNynorsk);
        URL url = new URL(targetUrl);

        return Jsoup.parse(url, 3000);
    }
}
