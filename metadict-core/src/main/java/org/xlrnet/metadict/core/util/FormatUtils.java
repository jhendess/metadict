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

package org.xlrnet.metadict.core.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.*;
import org.xlrnet.metadict.api.query.DictionaryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper class with various static methods for formatting Metadict objects to readable strings.
 */
public class FormatUtils {

    /**
     * Returns a formatted and human-readable string of a dictionary name. The result contains the name of each
     * language and dialect with the first letter of each word capitalized. If a language has a dialect, the dialect
     * will be written in parentheses. According to the dictionary direction, the two languages will be separated with
     * a different separator ({@code ->} or {@code <->}).
     * <p>
     * Example:
     * A dictionary unidirected dictionary de-en_gb will be formatted as "German -> English (Great Britain)".
     *
     * @param dictionary
     *         The dictionary to format.
     * @return a formatted and human-readable string of a dictionary name.
     */
    @NotNull
    public static String formatDictionaryName(@NotNull BilingualDictionary dictionary) {
        checkNotNull(dictionary);

        Language input = dictionary.getSource();
        Language output = dictionary.getTarget();
        boolean bidirectional = dictionary.isBidirectional();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(formatLanguage(input));

        if (bidirectional)
            stringBuilder.append(" <-> ");
        else
            stringBuilder.append(" -> ");

        stringBuilder.append(formatLanguage(output));
        return stringBuilder.toString();
    }

    @NotNull
    private static String formatLanguage(@NotNull Language language) {
        String displayName = StringUtils.strip(language.getDisplayName());
        String dialectDisplayName = StringUtils.stripToEmpty(language.getDialectDisplayName());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(WordUtils.capitalize(displayName));

        if (StringUtils.isNotEmpty(dialectDisplayName))
            stringBuilder.append(" (").append(WordUtils.capitalize(dialectDisplayName)).append(")");

        return stringBuilder.toString();
    }

    /**
     * Returns a formatted and human-readable representation of the additional information in a {@link
     * DictionaryObject}. This includes all data except for the general form and the {@link Language} object.
     * <p>
     * If the formatted object contains no additional information, an empty string will be returned.
     *
     * @param dictionaryObject
     *         The object whose additional information should be formatted.
     * @return A formatted string.
     */
    @NotNull
    public static String formatDictionaryObjectRepresentation(DictionaryObject dictionaryObject) {
        List<String> contentList = new ArrayList<>();

        CollectionUtils.addIgnoreNull(contentList, dictionaryObject.getDescription());

        if (dictionaryObject.getAdditionalForms().size() > 0) {
            Map<GrammaticalForm, String> additionalForms = dictionaryObject.getAdditionalForms();
            // Forms for nouns
            if (additionalForms.containsKey(GrammaticalNumber.SINGULAR))
                contentList.add("sg.: " + additionalForms.get(GrammaticalNumber.SINGULAR));
            if (additionalForms.containsKey(GrammaticalNumber.PLURAL))
                contentList.add("pl.: " + additionalForms.get(GrammaticalNumber.PLURAL));
            // Tenses
            if (additionalForms.containsKey(GrammaticalTense.PRESENT_TENSE))
                contentList.add("pr.: " + additionalForms.get(GrammaticalTense.PRESENT_TENSE));
            if (additionalForms.containsKey(GrammaticalTense.PAST_TENSE))
                contentList.add("pa.: " + additionalForms.get(GrammaticalTense.PAST_TENSE));
            if (additionalForms.containsKey(GrammaticalTense.PAST_PERFECT))
                contentList.add("par.: " + additionalForms.get(GrammaticalTense.PAST_PERFECT));
            if (additionalForms.containsKey(GrammaticalTense.PERFECT_PARTICIPLE))
                contentList.add("per.: " + additionalForms.get(GrammaticalTense.PERFECT_PARTICIPLE));
            // Adjective forms:
            if (additionalForms.containsKey(GrammaticalComparison.POSITIVE))
                contentList.add("pos.: " + additionalForms.get(GrammaticalComparison.POSITIVE));
            if (additionalForms.containsKey(GrammaticalComparison.COMPARATIVE))
                contentList.add("comp.: " + additionalForms.get(GrammaticalComparison.COMPARATIVE));
            if (additionalForms.containsKey(GrammaticalComparison.SUPERLATIVE))
                contentList.add("sup.: " + additionalForms.get(GrammaticalComparison.SUPERLATIVE));
            if (additionalForms.containsKey(GrammaticalCase.DEFINITE_FORM))
                contentList.add("def.: " + additionalForms.get(GrammaticalCase.DEFINITE_FORM));
        }

        if (dictionaryObject.getAbbreviation() != null) {
            contentList.add("abbr.: " + dictionaryObject.getAbbreviation());
        }

        if (dictionaryObject.getDomain() != null) {
            contentList.add("dom.: " + dictionaryObject.getDomain());
        }

        if (dictionaryObject.getGrammaticalGender() != null
                && !GrammaticalGender.NONE.equals(dictionaryObject.getGrammaticalGender())
                && !GrammaticalGender.UNKNOWN.equals(dictionaryObject.getGrammaticalGender())) {
            contentList.add("(" + dictionaryObject.getGrammaticalGender().getFormIdentifier() + ")");
        }

        return StringUtils.join(contentList, ", ");
    }
}
