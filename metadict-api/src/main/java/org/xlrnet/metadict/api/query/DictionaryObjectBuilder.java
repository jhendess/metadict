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

package org.xlrnet.metadict.api.query;

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.GrammaticalForm;
import org.xlrnet.metadict.api.language.GrammaticalGender;
import org.xlrnet.metadict.api.language.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for creating new {@link DictionaryObject} objects.
 */
public class DictionaryObjectBuilder {

    private Language language;

    private String generalForm;

    private String description;

    private String abbreviation;

    private String domain;

    private String pronunciation;

    private GrammaticalGender grammaticalGender;

    private Map<GrammaticalForm, String> additionalForms = new HashMap<>();

    private List<String> meanings = new ArrayList<>();

    private List<String> syllabification = new ArrayList<>();

    private List<String> alternateForms = new ArrayList<>();

    DictionaryObjectBuilder() {

    }

    /**
     * Add a new meaning to the current object.
     * <p/>
     * Example:
     * If the word is "bench", a meaning might be "A long seat for several people, typically made of wood or stone."
     *
     * @param newMeaning
     *         The new meaning that should be added.
     */
    @NotNull
    public DictionaryObjectBuilder addMeaning(@NotNull String newMeaning) {
        checkNotNull(newMeaning);
        this.meanings.add(newMeaning);

        return this;
    }

    /**
     * Add a new syllable to this object. The syllable may not be blank (i.e. all whitespace, empty or null).
     *
     * @param syllable
     *         The new syllable to add.
     */
    @NotNull
    public DictionaryObjectBuilder addSyllable(@NotNull String syllable) {
        checkArgument(StringUtils.isNotBlank(syllable));
        this.syllabification.add(syllable);

        return this;
    }

    /**
     * Adds all syllables of a given list to the syllables of this builder. This will not overwrite already added
     * syllables.
     *
     * @param syllabification A list of syllables to add.
     */
    @NotNull
    public DictionaryObjectBuilder setSyllabification(@NotNull List<String> syllabification) {
        checkNotNull(syllabification);

        for (String syllable : syllabification) {
            this.addSyllable(syllable);
        }

        return this;
    }

    /**
     * Adds all syllables of a given list to the syllables of this builder. This will not overwrite already added
     * syllables.
     *
     * @param syllabification A list of syllables to add.
     */
    @NotNull
    public DictionaryObjectBuilder setSyllabification(@NotNull String[] syllabification) {
        checkNotNull(syllabification);

        for (String syllable : syllabification) {
            this.addSyllable(syllable);
        }

        return this;
    }

    /**
     * Add a new alternately written form of the general form to this object.
     * <p/>
     * Example:
     * An alternative way of writing "color" might be "colour".
     *
     * @param alternateForm
     *         The new alternate form to add.
     */
    @NotNull
    public DictionaryObjectBuilder addAlternateForm(@NotNull String alternateForm) {
        checkArgument(StringUtils.isNotBlank(alternateForm));
        this.alternateForms.add(alternateForm);

        return this;
    }

    /**
     * Build a new instance of {@link DictionaryObject} with the set properties.
     *
     * @return a new instance of {@link DictionaryObject}.
     */
    @NotNull
    public DictionaryObject build() {
        checkNotNull(language, "Language may not be null");
        checkNotNull(generalForm, "General form may not be null");

        Optional<List<String>> optionalMeanings = Optional.absent();
        Optional<List<String>> optionalSyllabification = Optional.absent();
        Optional<List<String>> optionalAlternateForms = Optional.absent();

        if (meanings.size() > 0)
            optionalMeanings = Optional.of(meanings);
        if (syllabification.size() > 0)
            optionalSyllabification = Optional.of(syllabification);
        if (alternateForms.size() > 0)
            optionalAlternateForms = Optional.of(alternateForms);

        return new ImmutableDictionaryObject(language, generalForm, description, abbreviation, domain, pronunciation, grammaticalGender, additionalForms, optionalMeanings, optionalSyllabification, optionalAlternateForms);
    }

    /**
     * Sets an abbreviation for the new object.
     * <p/>
     * Example:
     * The abbreviation for "for example" is "e.g.".
     *
     * @param abbreviation
     *         An abbreviation for the new object.
     */
    @NotNull
    public DictionaryObjectBuilder setAbbreviation(String abbreviation) {
        checkNotNull(abbreviation);

        this.abbreviation = abbreviation;
        return this;
    }

    /**
     * Sets the value for a given irregular form of the new object. See {@link #setAdditionalForms(Map)}.
     *
     * @param key
     *         The key (i.e. grammatical form) for which a value should be stored.
     * @param value
     *         The value for the grammatical form.
     */
    @NotNull
    public DictionaryObjectBuilder setAdditionalForm(GrammaticalForm key, String value) {
        checkNotNull(key);
        checkNotNull(value);

        this.additionalForms.put(key, value);
        return this;
    }

    /**
     * Set the map of all irregular forms of the new object. These include e.g. special plural forms or
     * gender-dependent forms (in case of nouns) or irregular tenses on verbs.
     *
     * @return The map of all irregular forms of the new object.
     */
    @NotNull
    public DictionaryObjectBuilder setAdditionalForms(Map<GrammaticalForm, String> additionalForms) {
        checkNotNull(additionalForms);

        this.additionalForms = additionalForms;
        return this;
    }

    /**
     * Set a description for the object. The description field should be used for everything that is not a meaning
     * and does not fit in the other fields of this interface.
     *
     * @param description
     *         A description for the new object.
     */
    @NotNull
    public DictionaryObjectBuilder setDescription(String description) {
        checkNotNull(description);

        this.description = description;
        return this;
    }

    /**
     * Set the special domain the new object is used in. This can e.g. be something like "tech." if the object is only
     * used in technical contexts.
     *
     * @param domain
     *         The special domain the new object is used in.
     */
    @NotNull
    public DictionaryObjectBuilder setDomain(String domain) {
        checkNotNull(domain);

        this.domain = domain;
        return this;
    }

    /**
     * Set the general form of this object. The general form should be the most generic form of a word/phrase
     * whenever possible. This method should always return a non-null value, unless there is no specific general form
     * of a word.
     * That can e.g. be the case if the object represents an irregular word where each grammatical gender has a
     * different form and thus no general form exists.
     * <p/>
     * Example use cases:
     * <ul>
     * <li>Singular form of nouns</li>
     * <li>Present tense of verbs</li>
     * <li>Basic form of regular adjectives</li>
     * <li>Any type of phrase</li>
     * </ul>
     *
     * @param generalForm
     *         The general form of the new object.
     */
    @NotNull
    public DictionaryObjectBuilder setGeneralForm(String generalForm) {
        checkNotNull(generalForm);

        this.generalForm = generalForm;
        return this;
    }

    /**
     * Sets the grammatical gender of the new object. This field should be used on nouns (or other types) where the
     * general form has a grammatical gender.
     *
     * @param grammaticalGender
     *         The grammatical gender of the new object.
     */
    @NotNull
    public DictionaryObjectBuilder setGrammaticalGender(GrammaticalGender grammaticalGender) {
        checkNotNull(grammaticalGender);

        this.grammaticalGender = grammaticalGender;
        return this;
    }

    /**
     * Set the {@link Language} this object is written in.
     *
     * @param language
     *         The language the new object is written in.
     */
    @NotNull
    public DictionaryObjectBuilder setLanguage(Language language) {
        checkNotNull(language);

        this.language = language;
        return this;
    }

}
