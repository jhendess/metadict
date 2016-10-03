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
import org.xlrnet.metadict.api.language.GrammaticalGender;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
            .put("{m}", GrammaticalGender.MASCULINE)
            .put("{f}", GrammaticalGender.FEMININE)
            .put("{n}", GrammaticalGender.NEUTER)
            .build();

    private static final int TIMEOUT_MILLIS = 1500;

    private static final String CLASS_TRANSLATION = "dictionary-table-word";

    private static final String DESCRIPTION_BEGIN = "[";

    private static final String DESCRIPTION_END = "]";

    private static final String CLASS_GENDER = "word-gender";

    private static final String CLASS_WORDTYPE = "word-type";

    private static final String CLASS_DESCRIPTION = "word-description";

    private static final String CLASS_EXTRA_INFO = "word-extra-info";

    @NotNull
    @Override
    public BilingualQueryResult executeBilingualQuery(@NotNull String queryInput, @NotNull Language inputLanguage, @NotNull Language outputLanguage, boolean allowBothWay) throws Exception {
        Language targetLanguage = findTargetLanguage(inputLanguage, outputLanguage);
        URL targetUrl = buildTargetUrl(queryInput, targetLanguage);

        Document doc = Jsoup.parse(targetUrl, TIMEOUT_MILLIS);

        return processBilingualDocument(queryInput, doc, targetLanguage);
    }

    private BilingualQueryResult processBilingualDocument(@NotNull String queryInput, @NotNull Document doc, @NotNull Language targetLanguage) {
        BilingualQueryResultBuilder resultBuilder = ImmutableBilingualQueryResult.builder();

        processTranslationTable(queryInput, doc, resultBuilder, Language.GERMAN, targetLanguage);
        processTranslationTable(queryInput, doc, resultBuilder, targetLanguage, Language.GERMAN);

        findRecommendations(doc, resultBuilder);

        return resultBuilder.build();
    }

    private void extractBilingualSynonyms(@NotNull String queryString, @NotNull Element synonymsTable, @NotNull BilingualQueryResultBuilder resultBuilder, @NotNull Language sourceLanguage) {
        List<Element> synonymNodes = synonymsTable
                .select("tr")
                .stream()
                .filter(e -> e.getElementsByTag("th").size() == 0)
        .collect(Collectors.toList());

        if (synonymNodes.size() == 0) {
            LOGGER.debug("No synonym entries found");
            return;
        }

        String synonymEntryTitle = synonymsTable.select("span.hl").first().text();

        Map<String, SynonymGroupBuilder> synonymGroupMap = new HashMap<>();

        for (Element synonymNode : synonymNodes) {
            // Extract only information from the "from"-node (i.e. source language)
            DictionaryObject newSynonym = processSingleNode(synonymNode.getElementsByClass(CLASS_TRANSLATION).get(0), sourceLanguage, queryString);
            String groupName = newSynonym.getDescription() != null ? newSynonym.getDescription() : queryString;
            if (groupName != null) {
                SynonymGroupBuilder groupBuilder = synonymGroupMap.computeIfAbsent(groupName,
                        (s) -> ImmutableSynonymGroup.builder().setBaseMeaning(ImmutableDictionaryObject.createSimpleObject(sourceLanguage, s))
                );
                groupBuilder.addSynonym(newSynonym);
            } else {
                LOGGER.warn("Synonym group is null");
            }
        }

        SynonymEntryBuilder synonymEntryBuilder = ImmutableSynonymEntry.builder().setBaseObject(ImmutableDictionaryObject.createSimpleObject(sourceLanguage, synonymEntryTitle));

        for (SynonymGroupBuilder synonymGroupBuilder : synonymGroupMap.values()) {
            synonymEntryBuilder.addSynonymGroup(synonymGroupBuilder.build());
        }

        resultBuilder.addSynonymEntry(synonymEntryBuilder.build());
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

                DictionaryObjectBuilder objectBuilder = ImmutableDictionaryObject.builder();
                objectBuilder.setLanguage(currentLanguage).setGeneralForm(recommendationText);

                resultBuilder.addSimilarRecommendation(objectBuilder.build());
            }
        }
    }

    private void processTranslationTable(@NotNull String queryString, @NotNull Document document, @NotNull BilingualQueryResultBuilder resultBuilder, @NotNull Language sourceLanguage, @NotNull Language targetLanguage) {
        // Find main table (german to X)
        String languageIdentifier = sourceLanguage.getIdentifier().toLowerCase() + "-" + targetLanguage.getIdentifier().toLowerCase();

        Element translationTable = document.getElementById("dictionary-" + languageIdentifier);

        // Process the main table with its entries
        if (translationTable != null) {
            // Find all relevant entries, filter them by class and process them
            translationTable.getElementsByTag("tr")
                    .stream()
                    .filter(e -> e.getElementsByTag("th").size() == 0)
                    .forEach(e -> processEntry(queryString, e, resultBuilder, sourceLanguage, targetLanguage));
            // Extract synonyms
            Elements synonymTableCandidates = document.getElementsByClass("dictionary-synonyms-table");
            if (synonymTableCandidates.size() > 0) {
                extractBilingualSynonyms(queryString, synonymTableCandidates.get(0), resultBuilder, sourceLanguage);
            }

        } else {
            LOGGER.debug("Translation table for {} -> {} with query \"{}\" is null", languageIdentifier, targetLanguage.getIdentifier(), queryString);
        }
    }

    private void processEntry(@NotNull String queryString, @NotNull Element entryNode, @NotNull BilingualQueryResultBuilder resultBuilder, @NotNull Language sourceLanguage, @NotNull Language targetLanguage) {
        if (!StringUtils.equals(entryNode.tag().getName(), "tr")) {
            LOGGER.warn("Expected <tr> tag - got <{}>", entryNode.tag().getName());
            return;
        }
        Elements words = entryNode.getElementsByClass(CLASS_TRANSLATION);

        if (words.size() != 2) {
            LOGGER.warn("Expected 2 elements with class \"" + CLASS_TRANSLATION + "\" - got {}", words.size());
            return;
        }

        BilingualEntryBuilder entryBuilder = ImmutableBilingualEntry.builder();

        entryBuilder.setEntryType(detectEntryType(words.get(0)));
        entryBuilder.setInputObject(processSingleNode(words.get(0), sourceLanguage, queryString));
        entryBuilder.setOutputObject(processSingleNode(words.get(1), targetLanguage, queryString));

        resultBuilder.addBilingualEntry(entryBuilder.build());
    }

    @NotNull
    private DictionaryObject processSingleNode(@NotNull Element element, @NotNull Language language, String queryString) {
        DictionaryObjectBuilder objectBuilder = ImmutableDictionaryObject.builder();
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
        Element descriptionNode = element.getElementsByClass(CLASS_DESCRIPTION).first();
        if (descriptionNode == null) {
            // Try to detect the description node with an alternative class (necessary for synonyms)
            descriptionNode = element.getElementsByClass(CLASS_EXTRA_INFO).first();
        }
        if (descriptionNode != null) {
            String description = descriptionNode.text();

            description = StringUtils.removeStart(description, DESCRIPTION_BEGIN);
            description = StringUtils.removeEnd(description, DESCRIPTION_END);

            if (!StringUtils.equalsIgnoreCase(description, queryString))    // Set description only if it is different from request string
                objectBuilder.setDescription(StringUtils.strip(description));
        }
    }

    private void extractGender(@NotNull Element element, DictionaryObjectBuilder objectBuilder) {
        Element genderNode = element.getElementsByClass(CLASS_GENDER).first();
        if (genderNode != null) {
            String gender = genderNode.text();
            if (GENDER_MAP.containsKey(gender))
                objectBuilder.setGrammaticalGender(GENDER_MAP.get(gender));
        }
    }

    private EntryType detectEntryType(@NotNull Element element) {
        Elements wordTypeNodes = element.getElementsByClass(CLASS_WORDTYPE);

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
