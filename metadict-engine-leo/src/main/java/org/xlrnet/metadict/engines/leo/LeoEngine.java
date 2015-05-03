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

package org.xlrnet.metadict.engines.leo;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.GrammaticalNumber;
import org.xlrnet.metadict.api.language.GrammaticalTense;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.metadata.FeatureSet;
import org.xlrnet.metadict.api.query.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * This is the implementation for the leo.org search engine for Metadict. It uses the internal REST-endpoint from
 * leo.org that is used for their AJAX-calls.
 */
public class LeoEngine implements SearchEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeoEngine.class);

    private static final Map<String, EntryType> ENTRY_TYPE_MAP = ImmutableMap.<String, EntryType>builder()
            .put("subst", EntryType.NOUN)
            .put("noun", EntryType.NOUN)
            .put("adjv", EntryType.OTHER_WORD)      // Adjective or adverb
            .put("adverb", EntryType.ADVERB)
            .put("adjective", EntryType.ADJECTIVE)
            .put("verb", EntryType.VERB)
            .put("phrase", EntryType.PHRASE)
            .put("example", EntryType.EXAMPLE)
                    // TODO: Pronouns?
            .build();

    private static final Map<String, String> DEFAULT_QUERY_DATA = ImmutableMap.<String, String>builder()
            .put("tolerMode", "nof")
            .put("lang", "en")
            .put("rmWords", "off")
            .put("rmSearch", "on")
            .put("searchLoc", "0")
            .put("resultOrder", "basic")
            .put("multiwordShowSingle", "on")
            .put("sectLenMax", "16")
            .put("n", "1").build();

    private static final String SECTION_NAME_ATTRIBUTE = "sctName";

    /**
     * Strips various kinds of whitespace at the beginning and at the end of the input string.
     *
     * @param str
     *         The input string.
     * @return Stripped string.
     */
    private static String stripWhitespace(String str) {
        return StringUtils.strip(str, " \u00A0\n\t\r");
    }

    /**
     * The main method for querying a {@link SearchEngine}. This method will be called by the metadict core on incoming
     * search queries. The core will always try to parallelize the query as much as possible according to the specified
     * supported dictionaries of this engine.
     * <p>
     * Upon calling, the core will make sure that the language parameters of this method correspond exactly to a
     * supported {@link org.xlrnet.metadict.api.language.BilingualDictionary} as described in the engine's {@link
     * FeatureSet}. However, an engine may also return results from a different
     * language. In this case, the core component will decide it the supplied results are useful.
     * <p>
     * Example:
     * If the engine says it supports a one-way german-english dictionary, this method will be called with the language
     * parameters inputLanguage=GERMAN, outputLanguage=ENGLISH and allowBothWay=false.
     * However, it the engine supports a bidirectional german-english dictionary, this method will be called with the
     * language parameters inputLanguage=GERMAN, outputLanguage=ENGLISH and allowBothWay=true.
     *
     * @param queryInput
     *         The query string i.e. word that should be looked up.
     * @param inputLanguage
     *         The input language of the query. This language must be specified as a dictionary's input language of
     *         this engine.
     * @param outputLanguage
     *         The expected output language of the query. This language must be specified as the output language of the
     *         same dictionary to which the given inputLanguage belongs.
     * @param allowBothWay
     *         True, if the engine may search in both directions. I.e. the queryInput can also be seen as the
     *         outputLanguage. The core will set this flag only if the engine declared a dictionary with matching input
     *         and output language. Otherwise the will be called for each direction separately.
     * @return The results from the search query. You can use an instance of {@link BilingualQueryResultBuilder}
     * to build this result list.
     */
    @NotNull
    @Override
    public BilingualQueryResult executeBilingualQuery(@NotNull String queryInput, @NotNull Language inputLanguage, @NotNull Language outputLanguage, boolean allowBothWay) throws Exception {
        Connection targetConnection = buildTargetConnection(queryInput, inputLanguage, outputLanguage);
        Document doc = targetConnection.get();

        LOGGER.debug(doc.html());

        BilingualQueryResultBuilder builder = processDocument(doc);

        return builder.build();
    }

    /**
     * Try to extract the plural form if the automatic detection has failed.
     * <p>
     * Example:
     * "house - pl.: houses" should return "houses"
     *
     * @param inputString
     *         The input string.
     * @return the plural form or null if nothing could be found
     */
    private String alternativeExtractPluralString(String inputString) {
        int pluralIndex = StringUtils.indexOfIgnoreCase(inputString, "pl.:");
        if (pluralIndex < 0) return null;
        String pluralSubstring = StringUtils.substring(inputString, pluralIndex + 4);
        String substringTrim = StringUtils.substringBefore(pluralSubstring, "-");
        return stripWhitespace(substringTrim);
    }

    private Connection buildTargetConnection(String searchString, Language inputLanguage, Language outputLanguage) {
        String targetDictionary = resolveDictionaryConfig(inputLanguage, outputLanguage);
        if (targetDictionary == null) {
            targetDictionary = resolveDictionaryConfig(outputLanguage, inputLanguage);
            if (targetDictionary == null)
                throw new IllegalArgumentException("No suitable dictionary configuration found - this might be an internal metadict error");
        }

        return Jsoup.connect("https://dict.leo.org/dictQuery/m-vocab/" + targetDictionary + "/query.xml")
                .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .data(DEFAULT_QUERY_DATA)
                .data("lp", targetDictionary)
                .data("search", searchString)
                .data("t", ZonedDateTime.now(ZoneOffset.UTC).toString());
    }

    /**
     * Extracts the domain information from a given representation string.
     * <p>
     * Example:
     * If the input is "drive-in restaurant [cook.]", then the domain is "cook."
     *
     * @param representation
     *         The input string.
     * @return the domain string or null if none could be found
     */
    @Nullable
    private String extractAbbreviationString(String representation) {
        String substring = StringUtils.substringBetween(representation, "[abbr.:", "]");
        if (substring != null)
            return stripWhitespace(substring);
        return null;
    }

    /**
     * Extracts the domain information from a given representation string.
     * <p>
     * Example:
     * If the input is "drive-in restaurant [cook.]", then the domain is "cook."
     *
     * @param representation
     *         The input string.
     * @return the domain string or null if none could be found
     */
    @Nullable
    private String extractDomainString(String representation) {
        String substring = StringUtils.substringAfterLast(representation, "[");
        if (substring != null)
            return StringUtils.substringBefore(substring, "]");
        return null;
    }

    /**
     * Try to extract the best fitting general word form from an Elements object. If there are more than one general
     * form, the first one that contains parentheses or a dot (".") will be returned. If none contains parentheses, the
     * first element will be returned.
     *
     * @param side
     *         An Elements object of {@code <word>}-Tags
     * @return The general form of the word.
     */
    private String extractGeneralForm(Element side) {
        Elements elements = side.getElementsByTag("word");

        // TODO: Try to detect the correct form with "(sth.)" -> code below is not working

        /*if (elements.size() > 1) {
            for (Element element : elements) {
                String elementText = element.text();
                if ((elementText.contains("(") && elementText.contains(")")) || elementText.contains())
                    return elementText;
            }
        }*/
        return elements.first().text();
    }

    private BilingualQueryResultBuilder processDocument(Document doc) {
        BilingualQueryResultBuilder resultBuilder = new BilingualQueryResultBuilder();

        // Find sections:
        Elements sections = doc.getElementsByTag("section");

        // Process sections:
        sections.stream().parallel().forEach(s -> processSection(s, resultBuilder));

        // Find similarities:
        Element similarityNode = doc.getElementsByTag("similar").first();

        // Process similarities:
        processSimilarities(similarityNode, resultBuilder);

        // Find external contents:


        // Process external contents:


        return resultBuilder;
    }

    /**
     * Process the content contents of a single entry node. The entry node is the root-node for a single dictionary
     * entry.
     *
     * @param entryNode
     * @param resultBuilder
     * @param fallbackEntryType
     */
    private void processEntryNode(@NotNull Element entryNode, @NotNull BilingualQueryResultBuilder resultBuilder, @NotNull EntryType fallbackEntryType) {
        // Try to determine the entry type again
        EntryType entryType = fallbackEntryType;
        Element category = entryNode.getElementsByTag("category").first();
        if (category != null) {
            entryType = resolveSectionType(category.attr("type"));
            if (entryType == EntryType.UNKNOWN)
                entryType = fallbackEntryType;
        }

        // Process each side separately
        Elements sideNodes = entryNode.getElementsByTag("side");

        Element leftSide = sideNodes.get(0);
        Element rightSide = sideNodes.get(1);

        DictionaryObject leftObject = processSideNode(leftSide, entryType);
        DictionaryObject rightObject = processSideNode(rightSide, entryType);

        // Build the final DictionaryEntry
        resultBuilder.addBilingualEntry(new BilingualEntryBuilder()
                .setEntryType(entryType)
                .setInputObject(leftObject)
                .setOutputObject(rightObject).build());
    }

    private void processSection(Element sectionNode, BilingualQueryResultBuilder resultBuilder) {
        String sectionType = sectionNode.attr(SECTION_NAME_ATTRIBUTE);
        EntryType fallbackEntryType = resolveSectionType(sectionType);

        for (Element entryNode : sectionNode.getElementsByTag("entry")) {
            processEntryNode(entryNode, resultBuilder, fallbackEntryType);
        }
    }

    private DictionaryObject processSideNode(Element side, EntryType entryType) {
        DictionaryObjectBuilder dictionaryObjectBuilder = new DictionaryObjectBuilder();

        // Extract general form:
        String generalForm = extractGeneralForm(side);

        // Extract language:
        String languageIdentifier = side.attr("lang");
        if ("ch".equals(languageIdentifier))
            languageIdentifier = "cn";
        Language language = Language.getExistingLanguageById(languageIdentifier);

        final String[] pluralForm = new String[1];      // Workaround since objects inside lambda should be final

        // Extract description and plural form:
        side.getElementsByTag("small")
                .stream()
                .filter(e -> !StringUtils.startsWith(e.text(), "|"))        // Filter verb tenses!
                .forEach(element -> {
                    String elementText = element.text();
                    String elementHtml = element.outerHtml();
                    if (StringUtils.startsWithIgnoreCase(elementText, "pl.:")) {
                        pluralForm[0] = StringUtils.substringAfter(elementText, ".:");
                        if (StringUtils.isNotBlank(pluralForm[0]))
                            dictionaryObjectBuilder.setAdditionalForm(GrammaticalNumber.PLURAL, stripWhitespace(pluralForm[0]));
                    } else if (isValidDescriptionHtml(elementHtml)) {
                        elementText = StringUtils.strip(elementText, "-");
                        dictionaryObjectBuilder.setDescription(stripWhitespace(elementText));
                    }
                });

        String fullRepresentation = side.getElementsByTag("repr").text();

        // Test for domain specific content:
        String domain = extractDomainString(fullRepresentation);
        if (StringUtils.isNotBlank(domain))
            dictionaryObjectBuilder.setDomain(domain);

        // Test for abbreviation
        String abbreviation = extractAbbreviationString(fullRepresentation);
        if (StringUtils.isNotBlank(abbreviation))
            dictionaryObjectBuilder.setAbbreviation(abbreviation);

        // Try to detect alternative plural form:
        if (pluralForm[0] == null) {
            pluralForm[0] = alternativeExtractPluralString(fullRepresentation);
            if (StringUtils.isNotBlank(pluralForm[0]))
                dictionaryObjectBuilder.setAdditionalForm(GrammaticalNumber.PLURAL, pluralForm[0]);
        }

        // Process additional forms (e.g. verb tenses):
        String additionalFormText = side.getElementsByTag("repr").get(0).getElementsByTag("small").text();
        processTenses(entryType, dictionaryObjectBuilder, language, additionalFormText);

        return dictionaryObjectBuilder
                .setGeneralForm(generalForm)
                .setLanguage(language)
                .build();
    }

    private boolean isValidDescriptionHtml(String elementHtml) {
        return StringUtils.startsWithIgnoreCase(elementHtml, "<small><i>") && StringUtils.endsWith(elementHtml, "</i></small>")
                && !StringUtils.containsIgnoreCase(elementHtml, ".:") && !StringUtils.containsIgnoreCase(elementHtml, ".]")
                && !StringUtils.containsIgnoreCase(elementHtml, "auch:") && !StringUtils.containsIgnoreCase(elementHtml, "also:");
    }

    private void processSimilarities(@Nullable Element similarityNode, @NotNull EngineQueryResultBuilder engineQueryResultBuilder) {
        if (similarityNode == null) {
            LOGGER.warn("Couldn't find similarity node");
            return;
        }

        Elements sides = similarityNode.getElementsByTag("side");

        for (Element side : sides) {
            Language sideLanguage = Language.getExistingLanguageById(side.attr("lang"));

            for (Element word : side.getElementsByTag("word")) {
                String wordText = word.text();
                engineQueryResultBuilder.addSimilarRecommendation(
                        new DictionaryObjectBuilder()
                                .setLanguage(sideLanguage)
                                .setGeneralForm(wordText)
                                .build()
                );
            }
        }
    }

    private void processTenses(EntryType entryType, DictionaryObjectBuilder dictionaryObjectBuilder, Language language, String representation) {
        // Try to extract verb tenses in english  and german dictionary:
        if (entryType == EntryType.VERB && (Language.ENGLISH.equals(language) || Language.GERMAN.equals(language))) {
            String tensesString = StringUtils.substringBetween(representation, "|", "|");
            if (tensesString != null) {
                String[] tensesArray = StringUtils.split(tensesString, ",");
                if (tensesArray.length != 2) {
                    LOGGER.warn("Tenses array {} has unexpected length {} instead of 2", tensesArray, tensesArray.length);
                }
                dictionaryObjectBuilder.setAdditionalForm(GrammaticalTense.PAST_TENSE, stripWhitespace(tensesArray[0]));
                if (tensesArray.length >= 2)
                    dictionaryObjectBuilder.setAdditionalForm(GrammaticalTense.PAST_PERFECT, stripWhitespace(tensesArray[1]));
            }
        }
    }

    /**
     * Resolve the internal query configuration for the leo.org backend.
     * Currently supported:
     * <ul>
     * <li>German - English</li>
     * <li>German - French</li>
     * <li>German - Spanish</li>
     * <li>German - Italian</li>
     * <li>German - Chinese</li>
     * <li>German - Russian</li>
     * </ul>
     *
     * @param inputLanguage
     * @param outputLanguage
     */
    private String resolveDictionaryConfig(Language inputLanguage, Language outputLanguage) {
        switch (inputLanguage.getIdentifier()) {
            case "de":
                switch (outputLanguage.getIdentifier()) {
                    case "en":
                        return "ende";
                    case "fr":
                        return "frde";
                    case "es":
                        return "esde";
                    case "it":
                        return "itde";
                    case "cn":
                        return "chde";
                    case "ru":
                        return "rude";
                }
                break;
            case "en":
                switch (outputLanguage.getIdentifier()) {
                    case "de":
                        return "ende";
                }
                break;
            case "fr":
                switch (outputLanguage.getIdentifier()) {
                    case "de":
                        return "frde";
                }
                break;
            case "es":
                switch (outputLanguage.getIdentifier()) {
                    case "de":
                        return "esde";
                }
                break;
            case "it":
                switch (outputLanguage.getIdentifier()) {
                    case "de":
                        return "itde";
                }
                break;
            case "cn":
                switch (outputLanguage.getIdentifier()) {
                    case "de":
                        return "chde";
                }
                break;
            case "ru":
                switch (outputLanguage.getIdentifier()) {
                    case "de":
                        return "rude";
                }

        }
        LOGGER.warn("Unknown language configuration: {} - {}", inputLanguage, outputLanguage);
        return null;
    }

    private EntryType resolveSectionType(String sectionType) {
        return ENTRY_TYPE_MAP.getOrDefault(sectionType, EntryType.UNKNOWN);
    }
}
