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

package org.xlrnet.metadict.engines.heinzelnisse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.engine.FeatureSet;
import org.xlrnet.metadict.api.engine.SearchEngine;
import org.xlrnet.metadict.api.language.*;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.engines.heinzelnisse.entities.HeinzelResponse;
import org.xlrnet.metadict.engines.heinzelnisse.entities.TranslationEntry;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Engine for Heinzelnisse backend.
 */
public class HeinzelnisseEngine implements SearchEngine {

    private static final Map<String, EntryType> ENTRY_TYPE_MAP = ImmutableMap.<String, EntryType>builder()
            .put("m", EntryType.NOUN)
            .put("f", EntryType.NOUN)
            .put("n", EntryType.NOUN)
            .put("m/f", EntryType.NOUN)
            .put("v", EntryType.VERB)
            .put("adj", EntryType.ADJECTIVE)
            .put("adv", EntryType.ADVERB)
            .put("prep", EntryType.PREPOSITION)
            .put("pron", EntryType.PRONOUN)
            .put("interj", EntryType.INTERJECTION)
            .put("konj", EntryType.CONJUNCTION)
            .build();

    private static final Map<String, GrammaticalGender> GRAMMATICAL_GENDER_MAP = ImmutableMap.<String, GrammaticalGender>builder()
            .put("m", GrammaticalGender.MASCULINE)
            .put("f", GrammaticalGender.FEMININE)
            .put("m/f", GrammaticalGender.FEMININE)
            .put("n", GrammaticalGender.NEUTER)
            .build();


    private static final Logger LOGGER = LoggerFactory.getLogger(HeinzelnisseEngine.class);

    private static final String WIKI_BASE_URL = "http://www.heinzelnisse.info/wiki/";

    private final ObjectReader heinzelReader = new ObjectMapper().reader(HeinzelResponse.class);

    /**
     * The main method for querying a {@link SearchEngine}. This method will be called by the metadict core on incoming
     * search queries. The core will always try to parallelize the query as much as possible according to the specified
     * supported dictionaries of this engine.
     * <p>
     * Upon calling, the core will make sure that the language parameters of this method correspond exactly to a
     * supported {@link BilingualDictionary} as described in the engine's {@link FeatureSet}. However, an engine may
     * also return results from a different language. In this case, the core component will decide it the supplied
     * results are useful.
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
        boolean queryGerman = false, queryNorwegian = false;
        String requestedDictionary = BilingualDictionary.buildQueryString(inputLanguage, outputLanguage);

        switch (requestedDictionary) {
            case "de-no":
                if (Language.NORWEGIAN.equals(outputLanguage) || Language.NORWEGIAN_BOKMÅL.equals(outputLanguage)) {
                    if (allowBothWay)
                        queryNorwegian = true;
                    queryGerman = true;
                }
                break;
            case "no-de":
                if (Language.NORWEGIAN.equals(inputLanguage) || Language.NORWEGIAN_BOKMÅL.equals(inputLanguage)) {
                    if (allowBothWay)
                        queryGerman = true;
                    queryNorwegian = true;
                }
                break;
        }

        if (!queryGerman && !queryNorwegian)
            throw new UnsupportedDictionaryException(inputLanguage, outputLanguage, allowBothWay);

        return runQuery(queryInput, queryGerman, queryNorwegian);
    }

    /**
     * Extracts information from the "other"-fields of the response. This field may contain information about plural
     * forms or irregular verb forms.
     * <p>
     * Examples:
     * <ul>
     * <li>Plural: Mütter</li>
     * <li>fl.: mødre</li>
     * <li>syn.: tschüs</li>
     * <li>Dialekt (süddeutsch/österreichisch)</li>
     * <li>presens: kommer, preteritum: kom, partisipp perfekt: kommet</li>
     * <li>bestemt form: lille, intetkjønn: lite, flertall: små</li>
     * <li>komparativ: færre, superlativ: færrest</li>
     * <li>Komparativ: weniger, Superlativ: am wenigsten</li>
     * </ul>
     *
     * @param otherInformation
     *         The source string
     * @param builder
     *         The target builder to write into.
     */
    protected void extractOtherInformation(@NotNull String otherInformation, @NotNull DictionaryObjectBuilder builder) {
        // Try to extract plural forms
        if (StringUtils.startsWith(otherInformation, "Plural:") || StringUtils.startsWith(otherInformation, "fl.:")) {
            String pluralForm = StringUtils.substringAfter(otherInformation, ":");
            builder.setAdditionalForm(GrammaticalNumber.PLURAL, StringUtils.strip(pluralForm));
        }
        // Try to extract verb forms
        else if (StringUtils.startsWith(otherInformation, "presens")) {
            extractVerbForms(otherInformation, builder);
        }
        // Try to extract adjective comparisons
        else if (StringUtils.startsWithIgnoreCase(otherInformation, "komparativ")) {
            extractComparisonForms(otherInformation, builder);
        }
        // Try to extract adjective forms
        else if (StringUtils.startsWithIgnoreCase(otherInformation, "bestemt form")) {
            extractAdjectiveForms(otherInformation, builder);
        }
        // Write to description string otherwise...
        else if (StringUtils.isNotEmpty(otherInformation)) {
            builder.setDescription(StringUtils.strip(otherInformation));
        }
    }

    private void buildDictionaryObjects(@NotNull TranslationEntry entry, @NotNull BilingualEntryBuilder entryBuilder, boolean isGermanToNorwegian) {
        DictionaryObjectBuilder inBuilder = new DictionaryObjectBuilder();
        DictionaryObjectBuilder outBuilder = new DictionaryObjectBuilder();

        setBuilderLanguages(isGermanToNorwegian, inBuilder, outBuilder);

        // Extract non-translated word:
        inBuilder.setGeneralForm(entry.getWord());

        // Extract translated  word:
        outBuilder.setGeneralForm(entry.getTranslatedWord());

        extractArticleInformation(entry.getArticle(), inBuilder);               // Extract grammatical gender for input lang
        extractArticleInformation(entry.getTranslatedArticle(), outBuilder);    // Extract grammatical gender for output lang

        extractOtherInformation(entry.getOtherInformation(), inBuilder);            // Extract "other" information for input lang
        extractOtherInformation(entry.getTranslatedOtherInformation(), outBuilder); // Extract "other" information for output lang

        entryBuilder
                .setInputObject(inBuilder.build())
                .setOutputObject(outBuilder.build());
    }

    @NotNull
    private String buildTargetUrl(@NotNull String searchRequest, boolean onlyExactResults, boolean queryGerman, boolean queryNorwegian) throws UnsupportedEncodingException {
        StringBuilder targetUrlBuilder = new StringBuilder("http://www.heinzelnisse.info/searchResults?searchItem=")
                .append(URLEncoder.encode(searchRequest, "UTF-8"))
                .append("&dictExactSearch=");

        if (onlyExactResults) {
            targetUrlBuilder.append("on");
        }
        targetUrlBuilder.append("&type=json&setOptions=true&dictDeNoSearch=");

        if (queryGerman) {
            targetUrlBuilder.append("on");
        }

        targetUrlBuilder.append("&dictNoDeSearch=");

        if (queryNorwegian) {
            targetUrlBuilder.append("on");
        }

        targetUrlBuilder.append("on&dictPhoneticSearch=on&wikiSearch=on&dictNynorskSearch=on&dictBokmaalSearch=checked&forumKeywordSearch=on");

        return targetUrlBuilder.toString();
    }

    @NotNull
    private URLConnection buildUrlConnection(@NotNull URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        connection.setRequestProperty("Referrer", "http://www.heinzelnisse.info/app");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        return connection;
    }

    private void extractAdjectiveForms(@NotNull String otherInformation, @NotNull DictionaryObjectBuilder builder) {
        for (String s : StringUtils.split(otherInformation, ',')) {
            String flectedForm = StringUtils.strip(StringUtils.substringAfter(s, ":"));
            if (StringUtils.containsIgnoreCase(s, "bestemt form:")) {
                builder.setAdditionalForm(GrammaticalCase.DEFINITE_FORM, flectedForm);
            }
            if (StringUtils.containsIgnoreCase(s, "intetkjønn:")) {
                builder.setAdditionalForm(GrammaticalGender.NEUTER, flectedForm);
            }
            if (StringUtils.containsIgnoreCase(s, "flertall:")) {
                builder.setAdditionalForm(GrammaticalNumber.PLURAL, flectedForm);
            }
        }
    }

    /**
     * Extract the grammatical gender from a given article string and set it inside the given {@link
     * DictionaryObjectBuilder}. This method should only be applied to noun genders. If the grammatical gender couldn't
     * be identified, no value will be set in the builder.
     * <p>
     * Example for articles:
     * <ul>
     * <li>"f" -> {@link GrammaticalGender#FEMININE}</li>
     * <li>"m" -> {@link GrammaticalGender#MASCULINE}</li>
     * <li>...</li>
     * </ul>
     *
     * @param article
     *         The article identifier (e.g. "f", "m", "m/f", "n").
     * @param builder
     *         The builder to write into.
     */
    private void extractArticleInformation(@NotNull String article, @NotNull DictionaryObjectBuilder builder) {
        GrammaticalGender gender = GRAMMATICAL_GENDER_MAP.get(article);
        if (gender != null)
            builder.setGrammaticalGender(gender);
    }

    private void extractComparisonForms(@NotNull String otherInformation, @NotNull DictionaryObjectBuilder builder) {
        for (String s : StringUtils.split(otherInformation, ',')) {
            String flectedForm = StringUtils.strip(StringUtils.substringAfter(s, ":"));
            if (StringUtils.containsIgnoreCase(s, "komparativ:")) {
                builder.setAdditionalForm(GrammaticalComparison.COMPARATIVE, flectedForm);
            }
            if (StringUtils.containsIgnoreCase(s, "superlativ:")) {
                builder.setAdditionalForm(GrammaticalComparison.SUPERLATIVE, flectedForm);
            }
        }
    }

    private void extractVerbForms(@NotNull String otherInformation, @NotNull DictionaryObjectBuilder builder) {
        for (String s : StringUtils.split(otherInformation, ',')) {
            String flectedForm = StringUtils.strip(StringUtils.substringAfter(s, ":"));
            if (StringUtils.contains(s, "presens:")) {
                builder.setAdditionalForm(GrammaticalTense.PRESENT_TENSE, flectedForm);
            }
            if (StringUtils.contains(s, "preteritum:")) {
                builder.setAdditionalForm(GrammaticalTense.PAST_TENSE, flectedForm);
            }
            if (StringUtils.contains(s, "partisipp perfekt:")) {
                builder.setAdditionalForm(GrammaticalTense.PERFECT_PARTICIPLE, flectedForm);
            }
        }
    }

    private void extractWikiLinks(@NotNull List<String> wikiPageNames, @NotNull EngineQueryResultBuilder resultBuilder) {
        for (String wikiPage : wikiPageNames) {
            try {
                resultBuilder.addExternalContent(new ExternalContentBuilder()
                        .setTitle(wikiPage)
                        .setLink(new URL(WIKI_BASE_URL + wikiPage))
                        .build());
            } catch (MalformedURLException e) {
                LOGGER.warn("Extracting wiki link failed: {}", e);
            }
        }
    }

    /**
     * Runs a query with the specified request and exactmode against the Heinzelnisse backend. This method returns a
     * completely parsed {@see HeinzelResponse} object.
     *
     * @param searchRequest
     *         The request string that should be sent to the backend
     * @param onlyExactResults
     *         True, if only exact results should be returned.
     * @param queryGerman
     *         True, if German -> Norwegian shall be searched.
     * @param queryNorwegian
     *         True, if Norwegian -> German shall be searched.
     * @return the parsed result as an {@link HeinzelResponse}.
     * @throws IOException
     */
    private HeinzelResponse fetchResponse(@NotNull String searchRequest, boolean onlyExactResults, boolean queryGerman, boolean queryNorwegian) throws IOException {
        String targetUrl = buildTargetUrl(searchRequest, onlyExactResults, queryGerman, queryNorwegian);
        URL url = new URL(targetUrl);
        URLConnection connection = buildUrlConnection(url);
        return heinzelReader.readValue(connection.getInputStream());
    }

    private void processResponse(@NotNull HeinzelResponse heinzelResponse, @NotNull BilingualQueryResultBuilder resultBuilder, boolean queryGerman, boolean queryNorwegian) {
        // Extract german -> norwegian translations
        for (TranslationEntry entry : heinzelResponse.getGermanTranslations()) {
            processTranslationEntry(entry, resultBuilder, true);
        }
        // Extract norwegian -> german translations
        for (TranslationEntry entry : heinzelResponse.getNorwegianTranslations()) {
            processTranslationEntry(entry, resultBuilder, false);
        }

        // Extract similar recommendations by language:
        processSimilarRecomendations(heinzelResponse.getBookmaalWords(), Language.NORWEGIAN_BOKMÅL, resultBuilder);
        processSimilarRecomendations(heinzelResponse.getNynorskWords(), Language.NORWEGIAN_NYNORSK, resultBuilder);
        processSimilarRecomendations(heinzelResponse.getNorwegianPhonetics(), Language.NORWEGIAN_BOKMÅL, resultBuilder);
        processSimilarRecomendations(heinzelResponse.getGermanPhonetics(), Language.GERMAN, resultBuilder);

        // Extract wiki links:
        extractWikiLinks(heinzelResponse.getWikiPageNames(), resultBuilder);
    }

    private void processSimilarRecomendations(List<String> wordList, Language similarityLanguage, EngineQueryResultBuilder resultBuilder) {
        for (String word : wordList) {
            resultBuilder.addSimilarRecommendation(
                    new DictionaryObjectBuilder()
                            .setLanguage(similarityLanguage)
                            .setGeneralForm(word)
                            .build()
            );
        }
    }

    private void processTranslationEntry(@NotNull TranslationEntry entry, @NotNull BilingualQueryResultBuilder resultBuilder, boolean isGermanToNorwegian) {
        BilingualEntryBuilder entryBuilder = new BilingualEntryBuilder();
        // Resolve entry type
        EntryType entryType = resolveEntryType(entry);
        entryBuilder.setEntryType(entryType);
        // Build dictionary objects for the entry
        buildDictionaryObjects(entry, entryBuilder, isGermanToNorwegian);
        // Save in result builder
        resultBuilder.addBilingualEntry(entryBuilder.build());
    }

    @NotNull
    private EntryType resolveEntryType(@NotNull TranslationEntry entry) {
        return ENTRY_TYPE_MAP.getOrDefault(entry.getArticle(), EntryType.UNKNOWN);
    }

    private BilingualQueryResult runQuery(String queryInput, boolean queryGerman, boolean queryNorwegian) throws IOException {
        BilingualQueryResultBuilder resultBuilder = new BilingualQueryResultBuilder();

        HeinzelResponse fullResponse = fetchResponse(queryInput, false, queryGerman, queryNorwegian);
        //HeinzelResponse exactResponse = fetchResponse(queryInput, false, queryGerman, queryNorwegian);

        processResponse(fullResponse, resultBuilder, queryGerman, queryNorwegian);

        return resultBuilder.build();
    }

    private void setBuilderLanguages(boolean isGermanToNorwegian, DictionaryObjectBuilder inBuilder, DictionaryObjectBuilder outBuilder) {
        if (isGermanToNorwegian) {
            inBuilder.setLanguage(Language.GERMAN);
            outBuilder.setLanguage(Language.NORWEGIAN_BOKMÅL);
        } else {
            inBuilder.setLanguage(Language.NORWEGIAN_BOKMÅL);
            outBuilder.setLanguage(Language.GERMAN);
        }
    }
}
