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

package org.xlrnet.metadict.engines.woxikon;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.GrammaticalGender;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.metadata.FeatureSet;
import org.xlrnet.metadict.api.query.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Engine for woxikon.de backend. Internal methods work based on reverse-engineered HTML structures.
 */
public class WoxikonEngine implements SearchEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(WoxikonEngine.class);

    private static final String BASE_URL = "http://www.woxikon.de/";

    private static final Map<Language, String> BASE_URL_PER_LANGUAGE = ImmutableMap.<Language, String>builder()
            .put(Language.FRENCH, "deutsch-franzoesisch")
            .put(Language.SPANISH, "deutsch-spanisch")
            .put(Language.SWEDISH, "deutsch-schwedisch")
            .put(Language.RUSSIAN, "deutsch-russisch")
            .put(Language.FINNISH, "deutsch-finnisch")
            .put(Language.TURKISH, "deutsch-tuerkisch")
            .put(Language.ENGLISH, "deutsch-englisch")
            .put(Language.ITALIAN, "deutsch-italienisch")
            .put(Language.DUTCH, "deutsch-niederlaendisch")
            .put(Language.PORTUGUESE, "deutsch-portugiesisch")
            .put(Language.POLISH, "deutsch-polnisch")
            .put(Language.NORWEGIAN, "deutsch-norwegisch")
            .build();

    private static final Map<String, EntryType> ENTRY_TYPE_MAP = ImmutableMap.<String, EntryType>builder()
            .put("(a)", EntryType.ADJECTIVE)
            .put("(v)", EntryType.VERB)
            .put("(n)", EntryType.NOUN)
            .put("(o)", EntryType.OTHER_WORD)
            .build();

    private static final Map<String, GrammaticalGender> GENDER_MAP = ImmutableMap.<String, GrammaticalGender>builder()
            .put("[m.]", GrammaticalGender.MASCULINE)
            .put("[f.]", GrammaticalGender.FEMININE)
            .put("[n.]", GrammaticalGender.NEUTER)
            .build();

    /**
     * The main method for querying a {@link SearchEngine} with a <i>bilingual</i> search (i.e. translation). This
     * method will be called by the metadict core on incoming search queries. The core will always try to parallelize
     * the query as much as possible according to the specified supported dictionaries of this engine.
     * <p>
     * Upon calling, the core will make sure that the language parameters of this method correspond exactly to a
     * supported {@link BilingualDictionary} as described in the engine's {@link FeatureSet}. However, an engine may
     * also return results from a different language. In this case, the core component will decide if the supplied
     * results are useful.
     * <p>
     * Example: If the engine says it supports a one-way german-english dictionary, this method will be called with the
     * language parameters inputLanguage=GERMAN, outputLanguage=ENGLISH and allowBothWay=false. However, it the engine
     * supports a bidirectional german-english dictionary, this method will be called with the language parameters
     * inputLanguage=GERMAN, outputLanguage=ENGLISH and allowBothWay=true.
     *
     * @param queryInput
     *         The query string i.e. word that should be looked up.
     * @param inputLanguage
     *         The input language of the query. This language must be specified as a dictionary's input language of this
     *         engine.
     * @param outputLanguage
     *         The expected output language of the query. This language must be specified as the output language of the
     *         same dictionary to which the given inputLanguage belongs.
     * @param allowBothWay
     *         True, if the engine may search in both directions. I.e. the queryInput can also be seen as the
     *         outputLanguage. The core will set this flag only if the engine declared a dictionary with matching input
     *         and output language. Otherwise the will be called for each direction separately.
     * @return The results from the search query. You can use an instance of {@link BilingualQueryResultBuilder} to
     * build this result list.
     */
    @NotNull
    @Override
    public BilingualQueryResult executeBilingualQuery(@NotNull String queryInput, @NotNull Language inputLanguage, @NotNull Language outputLanguage, boolean allowBothWay) throws Exception {
        Language targetLanguage = findTargetLanguage(inputLanguage, outputLanguage);
        URL targetUrl = buildTargetUrl(queryInput, targetLanguage);

        Document doc = Jsoup.parse(targetUrl, 1000);

        return processBilingualDocument(queryInput, doc, targetLanguage);
    }

    private BilingualQueryResult processBilingualDocument(String queryInput, Document doc, Language targetLanguage) {
        BilingualQueryResultBuilder resultBuilder = new BilingualQueryResultBuilder();

        processTranslationTable(queryInput, doc, resultBuilder, Language.GERMAN, targetLanguage);
        processTranslationTable(queryInput, doc, resultBuilder, targetLanguage, Language.GERMAN);

        findRecommendations(doc, resultBuilder);

        return resultBuilder.build();
    }

    private void findRecommendations(@NotNull Document doc, @NotNull BilingualQueryResultBuilder resultBuilder) {
        // Determine all candidate nodes:
        Elements alternativeNodes = doc.select("div.cc > p > *");

        Language currentLanguage = null;

        for (Element node : alternativeNodes) {
            // If the next node is a flagicon, try to determine the language for the next entries from the class name
            if (node.tagName().equals("span") && node.hasClass("flagicon")) {
                Set<String> classNames = node.classNames();
                classNames.remove("flagicon");
                for (String className : classNames) {
                    Language candidate = Language.getExistingLanguageById(className);
                    if (candidate != null) {
                        currentLanguage = candidate;
                        break;
                    }
                }
            } else if (node.tagName().equals("a")) {
                String recommendationText = node.text();

                DictionaryObjectBuilder objectBuilder = new DictionaryObjectBuilder();
                objectBuilder.setLanguage(currentLanguage).setGeneralForm(recommendationText);

                resultBuilder.addSimilarRecommendation(objectBuilder.build());
            }
        }
    }

    private void processTranslationTable(@NotNull String queryString, @NotNull Document document, @NotNull BilingualQueryResultBuilder resultBuilder, @NotNull Language sourceLanguage, @NotNull Language targetLanguage) {
        // Find main table (german to X)
        String languageIdentifier = sourceLanguage.getIdentifier().toLowerCase();
        if (sourceLanguage.equals(Language.SWEDISH))
            languageIdentifier = "sv";

        Element translationTable = document.getElementById("gridTranslations-" + languageIdentifier);

        // Process the main table with its entries
        if (translationTable != null) {
            // Find all relevant entries, filter them by class and process them
            translationTable.getElementsByClass("hover")
                    .stream()
                    .filter(e -> e.hasClass("default") || e.hasClass("alt"))
                    .forEach(e -> processEntry(queryString, e, resultBuilder, sourceLanguage, targetLanguage));
        } else {
            LOGGER.debug("Translation table for {} -> {} with query \"{}\" is null", languageIdentifier, targetLanguage.getIdentifier(), queryString);
        }
    }

    private void processEntry(@NotNull String queryString, @NotNull Element entryNode, @NotNull BilingualQueryResultBuilder resultBuilder, @NotNull Language sourceLanguage, @NotNull Language targetLanguage) {
        if (!StringUtils.equals(entryNode.tag().getName(), "tr")) {
            LOGGER.warn("Expected <tr> tag - got <{}>", entryNode.tag().getName());
            return;
        }
        Elements words = entryNode.getElementsByClass("words");

        if (words.size() != 2) {
            LOGGER.warn("Expected 2 elements with class \"words\" - got {}", words.size());
            return;
        }

        BilingualEntryBuilder entryBuilder = new BilingualEntryBuilder();

        entryBuilder.setEntryType(detectEntryType(words.get(0)));
        entryBuilder.setInputObject(processSingleNode(words.get(0), sourceLanguage, queryString));
        entryBuilder.setOutputObject(processSingleNode(words.get(1), targetLanguage, queryString));

        resultBuilder.addBilingualEntry(entryBuilder.build());
    }

    @NotNull
    private DictionaryObject processSingleNode(@NotNull Element element, @NotNull Language language, String queryString) {
        DictionaryObjectBuilder objectBuilder = new DictionaryObjectBuilder();
        objectBuilder.setLanguage(language);

        // Extract entry text:
        String context = StringUtils.substringBefore(element.text(), element.getElementsByTag("a").first().text());
        String generalForm = context + element.getElementsByTag("a").first().text();
        objectBuilder.setGeneralForm(StringUtils.strip(generalForm));

        // Extract description:
        extractDescription(element, queryString, objectBuilder);

        // Extract gender:
        extractGender(element, objectBuilder);

        return objectBuilder.build();
    }

    private void extractDescription(@NotNull Element element, String queryString, DictionaryObjectBuilder objectBuilder) {
        Element descriptionNode = element.getElementsByClass("info").first();
        if (descriptionNode != null) {
            String description = descriptionNode.text();

            description = StringUtils.removeStart(description, "(");
            description = StringUtils.removeEnd(description, ")");

            if (!StringUtils.equalsIgnoreCase(description, queryString))    // Set description only if it is different from request string
                objectBuilder.setDescription(StringUtils.strip(description));
        }
    }

    private void extractGender(@NotNull Element element, DictionaryObjectBuilder objectBuilder) {
        Element genderNode = element.getElementsByClass("gender").first();
        if (genderNode != null) {
            String gender = genderNode.text();
            if (GENDER_MAP.containsKey(gender))
                objectBuilder.setGrammaticalGender(GENDER_MAP.get(gender));
        }
    }

    private EntryType detectEntryType(@NotNull Element element) {
        Elements wordTypeNodes = element.getElementsByClass("wordType");

        if (wordTypeNodes.size() < 1) {
            LOGGER.debug("No wordType node found - defaulting to {}", EntryType.UNKNOWN);
            return EntryType.UNKNOWN;
        }

        EntryType entryType = ENTRY_TYPE_MAP.getOrDefault(wordTypeNodes.first().text(), EntryType.UNKNOWN);

        if (entryType == EntryType.UNKNOWN)
            LOGGER.debug("Unable to resolve entry type \"{}\"", entryType);

        return entryType;
    }


    /**
     * Returns the one of two languages that is not GERMAN. If none of the two is german, an IllegalArgumentException
     * will be thrown.
     *
     * @param inputLanguage
     *         First language to compare.
     * @param outputLanguage
     *         Second language to compare.
     * @return The non-german language.
     */
    @NotNull
    private Language findTargetLanguage(@NotNull Language inputLanguage, @NotNull Language outputLanguage) {

        if (Language.GERMAN.equals(inputLanguage))
            return outputLanguage;
        else if (Language.GERMAN.equals(outputLanguage)) {
            return inputLanguage;
        } else {
            throw new IllegalArgumentException("Expected at least one language to be german - got " + inputLanguage.getIdentifier() + " and " + outputLanguage.getDisplayName());
        }
    }

    @NotNull
    private URL buildTargetUrl(@NotNull String queryString, @NotNull Language language) throws UnsupportedEncodingException, MalformedURLException {
        if (!BASE_URL_PER_LANGUAGE.containsKey(language))
            throw new IllegalArgumentException("Unsupported language request: " + language.toString());

        String encodedQueryString = URLEncoder.encode(queryString, "UTF-8");
        return new URL(BASE_URL + BASE_URL_PER_LANGUAGE.get(language) + "/" + encodedQueryString + ".php");
    }

}
