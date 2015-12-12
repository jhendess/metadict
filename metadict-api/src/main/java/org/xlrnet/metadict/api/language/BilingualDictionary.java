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

package org.xlrnet.metadict.api.language;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.engine.FeatureSet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class {@link BilingualDictionary} is used to describe the properties of the languages a bilingual dictionary
 * supports. Each dictionary has a direction, i.e. English-German is not the same as German-English. However, it is
 * possible to declare a bidirectional dictionary. When a dictionary is bidirectional, the implementing {@link
 * org.xlrnet.metadict.api.engine.SearchEngine} may then provide words in both directions without being called multiple
 * times.
 * <p>
 * A monolingual dictionary can be described with just a plain {@link Language} object. See the according documentation
 * for {@link FeatureSet} and {@link org.xlrnet.metadict.api.engine.SearchEngine}.
 */
public class BilingualDictionary implements Serializable, Comparable<BilingualDictionary> {

    private static final Pattern DICTIONARY_QUERY_PATTERN = Pattern.compile("([A-z]+(_[A-z]+)?-[A-z]+(_[A-z]+)?)");

    private static final Map<String, BilingualDictionary> instanceMap = new HashMap<>();

    private static final long serialVersionUID = -1851855085963129942L;

    private final Language input;

    private final Language output;

    private final boolean bidirectional;

    private final String queryString;

    private final String queryStringWithDialect;

    private BilingualDictionary(@NotNull Language input, @NotNull Language output, boolean bidirectional) {
        this.input = input;
        this.output = output;
        this.bidirectional = bidirectional;
        this.queryString = buildQueryString(input, output);
        this.queryStringWithDialect = buildQueryStringWithDialect(input, output);
    }

    /**
     * Builds the query string for a given {@link Language} object.
     * Each dictionary's language identifier is separated with a minus ("-"). This method does not respect dialects.
     * <p>
     * Example: "de-en" is the query string for a dictionary from german ("de") to english ("en")
     *
     * @param input
     *         The input language.
     * @param output
     *         The output language.
     * @return a dictionary query string
     */
    @NotNull
    public static String buildQueryString(@Nullable Language input, @Nullable Language output) {
        checkNotNull(input);
        checkNotNull(output);

        StringBuilder idBuilder = new StringBuilder().append(input.getIdentifier());
        idBuilder.append("-").append(output.getIdentifier());
        return idBuilder.toString();
    }

    /**
     * Builds the query string for two given {@link Language} objects.
     * Each dictionary's language identifier is separated with a minus ("-"). If a concrete dialect is available,
     * an underscore will be set ("_") after the language identifiers followed by the dialect identifier.
     * <p>
     * Example: "de-no_ny" is the query string for a dictionary from german to norwegian nynorsk (i.e. the identifier
     * "de" and the dialect "ny" of language "no").
     *
     * @param input
     *         The input language.
     * @param output
     *         The output language.
     * @return a dictionary query string
     */
    @NotNull
    public static String buildQueryStringWithDialect(@Nullable Language input, @Nullable Language output) {
        checkNotNull(input);
        checkNotNull(output);

        StringBuilder idBuilder = new StringBuilder().append(input.getIdentifier());
        if (StringUtils.isNotEmpty(input.getDialect()))
            idBuilder.append("_").append(input.getDialect());
        idBuilder.append("-").append(output.getIdentifier());
        if (StringUtils.isNotEmpty(output.getDialect()))
            idBuilder.append("_").append(output.getDialect());
        return idBuilder.toString();
    }

    /**
     * Create a new instance of {@link BilingualDictionary} or lookup if such a configuration already exists in the
     * internal cache.
     *
     * @param input
     *         The input language of the new dictionary.
     * @param output
     *         The input language of the new dictionary.
     * @param bidirectional
     *         If set to true, the dictionary supports looking up entries in both directions.
     * @return a new instance of {@link BilingualDictionary} or lookup if such a configuration already exists in the
     * internal cache.
     */
    @NotNull
    public static BilingualDictionary fromLanguages(@NotNull Language input, @NotNull Language output, boolean bidirectional) {
        checkNotNull(input, "Input language for dictionary may not be null");
        checkNotNull(output, "Output language for dictionary may not be null");

        StringBuilder builder = new StringBuilder();

        if (bidirectional)
            builder.append("<>");

        builder.append(input.getIdentifier());
        if (!StringUtils.isEmpty(input.getDialect()))
            builder.append("_").append(input.getDialect());
        builder.append("-").append(output.getIdentifier());
        if (!StringUtils.isEmpty(output.getDialect()))
            builder.append("_").append(output.getDialect());

        String query = builder.toString();

        return instanceMap.computeIfAbsent(query, (k) -> new BilingualDictionary(input, output, bidirectional));
    }

    /**
     * Find an existing dictionary from a dictionary query string.  A dictionary query string is a comma-separated list
     * of dictionaries. Each dictionary's language is separated with a minus ("-"). If you need a concrete dialect,
     * use an underscore ("_") after the language identifiers. Note, that this does not create new {@link
     * BilingualDictionary} object but find existing ones.
     * <p>
     * Example: "de-en,de-no_ny" will find a bilingual dictionary between german and english (i.e. the two identifiers
     * "de" and "en") and also another dictionary with german and norwegian nynorsk (i.e. the identifier "de" and the
     * dialect "ny" of language "no").
     *
     * @param queryString
     *         The query string - see method description body.
     * @return all dictionaries matching the query.
     * @throws IllegalArgumentException
     *         Will be thrown if the given dictionary query is invalid.
     */
    @NotNull
    public static BilingualDictionary fromQueryString(String queryString, boolean bidirectional) throws IllegalArgumentException, UnsupportedDictionaryException {
        if (!isValidDictionaryQuery(queryString))
            throw new IllegalArgumentException("Illegal dictionary query string: " + queryString);

        if (bidirectional) {
            if (instanceMap.containsKey("<>" + queryString)) {
                return instanceMap.get("<>" + queryString);
            } else {
                // Inverse the direction and try another lookup
                String[] languages = StringUtils.split(queryString, "-");
                String inverseQuery = "<>" + languages[1] + "-" + languages[0];
                if (instanceMap.containsKey(inverseQuery))
                    return instanceMap.get(inverseQuery);
                else
                    throw new UnsupportedDictionaryException(
                            Language.getExistingLanguageById(languages[0]),
                            Language.getExistingLanguageById(languages[1]),
                            true);
            }
        } else
            return instanceMap.get(queryString);
    }

    /**
     * Inverses the language direction of the given {@link BilingualDictionary} object.
     * <p>
     * Example:
     * If the given dictionary is de-en this method will return a dictionary with en-de. The bidirectional state will
     * not change.
     *
     * @param dictionary
     *         The dictionary object to invert.
     * @return A dictionary with inversed in- and output languages.
     */
    @NotNull
    public static BilingualDictionary inverse(@NotNull BilingualDictionary dictionary) {
        return BilingualDictionary.fromLanguages(dictionary.getOutput(), dictionary.getInput(), dictionary.isBidirectional());
    }

    /**
     * Checks if the given matches a dictionary query (only a single). Each language is separated with a
     * minus ("-"). If you need a concrete dialect, use an underscore ("_") after the language identifiers.
     *
     * @param queryString
     *         The query to check.
     * @return True if valid dictionary query, false otherwise.
     */
    private static boolean isValidDictionaryQuery(String queryString) {
        return DICTIONARY_QUERY_PATTERN.matcher(queryString).matches();
    }

    /**
     * Returns the expected input language of this dictionary.
     *
     * @return the expected input language of this dictionary.
     */
    @NotNull
    public Language getInput() {
        return input;
    }

    /**
     * Returns the expected output language of this dictionary.
     *
     * @return the expected output language of this dictionary.
     */
    @NotNull
    public Language getOutput() {
        return output;
    }

    @NotNull
    public String getQueryString() {
        return queryString;
    }

    @NotNull
    public String getQueryStringWithDialect() {
        return queryStringWithDialect;
    }

    /**
     * Returns whether the dictionary supports a bidirectional search. When a dictionary is bidirectional, the
     * implementing {@link org.xlrnet.metadict.api.engine.SearchEngine} may then provide words in both directions
     * without being called multiple times.
     *
     * @return whether the dictionary supports a direct bidirectional search.
     */
    public boolean isBidirectional() {
        return bidirectional;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("input", input)
                .add("output", output)
                .add("bidirectional", bidirectional)
                .toString();
    }

    @Override
    public int compareTo(@NotNull BilingualDictionary bilingualDictionary) {
        if (!this.getInput().equals(bilingualDictionary.getInput())) {
            return this.getInput().getDisplayName().compareTo(bilingualDictionary.getInput().getDisplayName());
        } else {
            return this.getOutput().getDisplayName().compareTo(bilingualDictionary.getOutput().getDisplayName());
        }
    }
}
