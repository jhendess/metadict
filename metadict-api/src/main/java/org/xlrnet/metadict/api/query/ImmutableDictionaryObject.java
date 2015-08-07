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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.language.GrammaticalForm;
import org.xlrnet.metadict.api.language.GrammaticalGender;
import org.xlrnet.metadict.api.language.Language;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable implementation of {@link DictionaryObject},
 */
public class ImmutableDictionaryObject implements DictionaryObject {

    private static final long serialVersionUID = 742684176144148121L;

    private final Language language;

    private final String generalForm;

    private final String description;

    private final String abbreviation;

    private final String domain;

    private final String pronunciation;

    private final GrammaticalGender grammaticalGender;

    private final Map<GrammaticalForm, String> additionalForms;

    private final Optional<List<String>> meanings;

    private final Optional<List<String>> syllabification;

    private final Optional<List<String>> alternateForms;

    /**
     * Create a new immutable instance. See {@link DictionaryObject} for more information about the parameters.
     *
     * @param language
     *         The language this object is written in.
     * @param generalForm
     *         The general form of this object.
     * @param description
     *         The description for this object.
     * @param abbreviation
     *         The abbreviation for this object.
     * @param domain
     *         The special domain for this object.
     * @param pronunciation
     *         An IPA-like written string that shows how to pronounce the general form of this object.
     * @param grammaticalGender
     *         The grammatical gender of this object.
     * @param additionalForms
     *         A map of additional forms this object may have.
     * @param meanings
     *         A list of different meanings for this have.
     * @param syllabification
     *         The syllabification of this object, represented with each syllable as a list-element.
     * @param alternateForms
     *         Alternate ways of writing this object.
     */
    ImmutableDictionaryObject(Language language, String generalForm, String description, String abbreviation, String domain, String pronunciation, GrammaticalGender grammaticalGender, Map<GrammaticalForm, String> additionalForms, Optional<List<String>> meanings, Optional<List<String>> syllabification, Optional<List<String>> alternateForms) {
        this.language = language;
        this.generalForm = generalForm;
        this.description = description;
        this.abbreviation = abbreviation;
        this.domain = domain;
        this.pronunciation = pronunciation;
        this.grammaticalGender = grammaticalGender;
        this.additionalForms = additionalForms;
        this.meanings = meanings;
        this.syllabification = syllabification;
        this.alternateForms = alternateForms;
    }

    /**
     * Return a new builder instance for creating new {@link DictionaryObject} objects.
     *
     * @return a new builder.
     */
    @NotNull
    public static DictionaryObjectBuilder builder() {
        return new DictionaryObjectBuilder();
    }

    /**
     * Create a new {@link DictionaryObject} with only a language and a general form.
     *
     * @param language
     *         The language this object is written in.
     * @param generalForm
     *         The general form of this object.
     * @return a dictionary object with only language and general form set.
     */
    @NotNull
    public static DictionaryObject createSimpleObject(@NotNull Language language, @NotNull String generalForm) {
        checkNotNull("General form may not be null", generalForm);
        checkNotNull("Language may not be null", language);

        return new ImmutableDictionaryObject(language, generalForm, null, null, null, null, null, null, null, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableDictionaryObject)) return false;
        ImmutableDictionaryObject that = (ImmutableDictionaryObject) o;
        return Objects.equal(language, that.language) &&
                Objects.equal(generalForm, that.generalForm) &&
                Objects.equal(description, that.description) &&
                Objects.equal(abbreviation, that.abbreviation) &&
                Objects.equal(domain, that.domain) &&
                Objects.equal(grammaticalGender, that.grammaticalGender) &&
                Objects.equal(additionalForms, that.additionalForms) &&
                Objects.equal(meanings, that.meanings) &&
                Objects.equal(syllabification, that.syllabification);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(language, generalForm, description, abbreviation, domain, grammaticalGender, additionalForms, meanings, syllabification);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("language", language)
                .add("generalForm", generalForm)
                .add("description", description)
                .add("abbreviation", abbreviation)
                .add("domain", domain)
                .add("grammaticalGender", grammaticalGender)
                .add("additionalForms", additionalForms)
                .add("meanings", meanings)
                .add("syllabification", syllabification)

                .toString();
    }

    /**
     * Returns an abbreviation for this object.
     * <p/>
     * Example:
     * The abbreviation for "for example" is "e.g.".
     *
     * @return an abbreviation for this object.
     */
    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Returns a map of all irregular forms of this object. These include e.g. special plural forms or gender-dependent
     * forms (in case
     * of nouns) or irregular tenses on verbs.
     *
     * @return a map of all irregular forms of this object.
     */
    @NotNull
    @Override
    public Map<GrammaticalForm, String> getAdditionalForms() {
        return additionalForms;
    }

    /**
     * Returns a description for the object. The description field should be used for everything that is not a meaning
     * and does not fit in the other fields of this interface.
     *
     * @return a description for the object.
     */
    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Returns the special domain this object is used in. This can e.g. be something like "tech." if the object is only
     * used in technical contexts.
     *
     * @return the special domain this object is used in.
     */
    @Nullable
    @Override
    public String getDomain() {
        return domain;
    }

    /**
     * Returns the general form of this object. The general form should be the most generic form of a word/phrase
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
     * @return the general form of this object.
     */
    @NotNull
    @Override
    public String getGeneralForm() {
        return generalForm;
    }

    /**
     * Returns the grammatical gender of this object. This field should be used on nouns (or other types) where the
     * general form has a grammatical gender.
     *
     * @return the grammatical gender of this object.
     */
    @Nullable
    @Override
    public GrammaticalGender getGrammaticalGender() {
        return grammaticalGender;
    }

    /**
     * Returns the {@link Language} this object is written in. The language has to be set always and may not be null.
     *
     * @return the language this object is written in.
     */
    @NotNull
    @Override
    public Language getLanguage() {
        return language;
    }

    /**
     * Returns a string with a pronunciation of the general form of this entry. The pronunciation should be written
     * down using the international phonetic alphabet (IPA).
     *
     * @return a string with a pronunciation of the general form of this entry.
     */
    @Nullable
    @Override
    public String getPronunciation() {
        return this.pronunciation;
    }

    /**
     * Returns a list of different meanings this objects may have. Each meaning's description should be a separate
     * string.
     * <p/>
     * Example:
     * If the word is "bench", a meaning might be "A long seat for several people, typically made of wood or stone."
     *
     * @return a list of different meanings this objects may have.
     */
    @NotNull
    @Override
    public Optional<List<String>> getMeanings() {
        return meanings;
    }

    /**
     * Returns a list of syllables that this word consists of. Each element of the list represents a single syllable.
     * <p/>
     * Example:
     * If a word is divided like <i>dic|tion|ary</i>, the list should look like {"dic", "tion", "ary" }.
     *
     * @return a list of syllables that this word consists of.
     */
    @NotNull
    @Override
    public Optional<List<String>> getSyllabification() {
        return syllabification;
    }

    /**
     * Returns a list of alternately written forms for this entry.
     * <p/>
     * Example:
     * An alternative way of writing "color" might be "colour".
     *
     * @return a list of alternately written forms for this entry.
     */
    @NotNull
    @Override
    public Optional<List<String>> getAlternateForms() {
        return this.alternateForms;
    }

}