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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The enum {@link Language} is used to describe the language of a word. Each language must have at least an identifier
 * and a human-readable displayName. Although it is not necessary, it is recommended to use an ISO 639-1-compliant
 * string as identifier. <br/> A language can also contain an optional dialect String.
 */
public class Language {

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[A-Za-z]+");

    private static final ConcurrentMap<String, Language> languageCache = new ConcurrentHashMap<>();

    /**
     * Preconfigured language for English.
     */
    public static final Language ENGLISH = forSimpleLanguage("en", "English");

    /**
     * Preconfigured language for German.
     */
    public static final Language GERMAN = forSimpleLanguage("de", "German");

    /**
     * Preconfigured language for french.
     */
    public static final Language FRENCH = forSimpleLanguage("fr", "French");

    /**
     * Preconfigured language for Spanish.
     */
    public static final Language SPANISH = forSimpleLanguage("es", "Spanish");

    /**
     * Preconfigured language for Italian.
     */
    public static final Language ITALIAN = forSimpleLanguage("it", "Italian");

    /**
     * Preconfigured language for Chinese.
     */
    public static final Language CHINESE = forSimpleLanguage("cn", "Chinese");

    /**
     * Preconfigured language for Russian.
     */
    public static final Language RUSSIAN = forSimpleLanguage("ru", "Russian");

    /**
     * Preconfigured language for Norwegian (either bokmål or nynorsk).
     */
    public static final Language NORWEGIAN = forSimpleLanguage("no", "Norwegian");

    /**
     * Preconfigured language for Swedish.
     */
    public static final Language SWEDISH = forSimpleLanguage("se", "Swedish");

    /**
     * Preconfigured language for Finnish.
     */
    public static final Language FINNISH = forSimpleLanguage("fi", "Finnish");

    /**
     * Preconfigured language for Turkish.
     */
    public static final Language TURKISH = forSimpleLanguage("tr", "Turkish");

    /**
     * Preconfigured language for Dutch.
     */
    public static final Language DUTCH = forSimpleLanguage("nl", "Dutch");

    /**
     * Preconfigured language for Portuguese.
     */
    public static final Language PORTUGUESE = forSimpleLanguage("pt", "Portuguese");

    /**
     * Preconfigured language for Polish.
     */
    public static final Language POLISH = forSimpleLanguage("pl", "Polish");

    private static final ConcurrentMap<String, Language> dialectCache = new ConcurrentHashMap<>();

    /**
     * Preconfigured language for Norwegian bokmål.
     */
    public static final Language NORWEGIAN_BOKMÅL = forSimpleLanguage("no", "Norwegian", "bo", "Bokmål");

    /**
     * Preconfigured language for Norwegian nynorsk.
     */
    public static final Language NORWEGIAN_NYNORSK = forSimpleLanguage("no", "Norwegian", "ny", "Nynorsk");

    private final String identifier;

    private final String displayName;

    private final String dialect;

    private final String dialectDisplayName;

    protected Language(String identifier, String displayName, String dialect, String dialectDisplayName) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.dialect = dialect;
        this.dialectDisplayName = dialectDisplayName;
    }

    /**
     * Either creates a new language with the given identifier or returns the already registered language object for the
     * given identifier. The identifier will always be converted to lower-case and is therefore case-insensitive. Valid
     * identifiers consist of only characters (a to z) and must be at least one character long.
     *
     * @param identifier
     *         The identifier for the language. It is recommended to use an ISO 639-1-compliant string. See format
     *         restrictions above.
     * @param displayName
     *         The name of the language that will be displayed to the client. If the language is already cached, the
     *         registered displayname will be returned.
     * @return A {@link Language} object with the given identifier.
     */
    @NotNull
    public static Language forSimpleLanguage(String identifier, String displayName) throws IllegalArgumentException {
        checkNotNull(identifier, "Language identifier may not be null");
        checkNotNull(displayName, "Display name may not be null");
        checkArgument(isValidIdentifier(identifier), "Invalid language identifier: %s", identifier);

        String key = identifier.toLowerCase();
        languageCache.putIfAbsent(key, new Language(key, displayName, null, null));
        return languageCache.get(key);
    }

    /**
     * Return the {@link Language} object that is currently associated with the given identifier. This call will not
     * create a new object but only return existing ones - not including dialects.
     *
     * @param identifier
     *         the language object that is currently associated with the given identifier.
     * @return Either the resulting language or null if nothing found.
     */
    @Nullable
    public static Language getExistingLanguageById(String identifier) {
        checkNotNull(identifier, "Language identifier may not be null");

        return languageCache.get(identifier.toLowerCase());
    }

    /**
     * Checks if the given string is a valid language identifier. Valid identifiers consist of only characters (a to z)
     * and must be at least one character long
     *
     * @param identifier
     *         The string that should be tested for validity.
     * @return true if the given string is a valid identifier, otherwise false.
     */
    static boolean isValidIdentifier(String identifier) {
        return IDENTIFIER_PATTERN.matcher(identifier).matches();
    }

    /**
     * Either creates a new language with the given identifier and given dialect identifier or returns the already
     * registered language object for the given identifiers. The identifiers will always be converted to lower-case and
     * are therefore case-insensitive. Valid identifiers consist of only characters (a to z) and must be at least one
     * character long.
     *
     * @param identifier
     *         The identifier for the language. It is recommended to use an ISO 639-1-compliant string. See format
     *         restrictions above.
     * @param displayName
     *         The name of the language that will be displayed to the client. If the language is already cached, the
     *         registered displayname will be returned.
     * @param dialectIdentifier
     *         The identifier for the dialect.
     * @param dialectDisplayName
     *         The name of the dialect that will be displayed to the client. If the dialect is already cached, the
     *         registered dialect's displayname will be returned.
     * @return A {@link Language} object with the given identifier.
     */
    public static Language forSimpleLanguage(String identifier, String displayName, String dialectIdentifier, String dialectDisplayName) {
        checkNotNull(identifier, "Language identifier may not be null");
        checkNotNull(displayName, "Display name may not be null");
        checkNotNull(dialectIdentifier, "Dialect identifier may not be null");
        checkNotNull(dialectDisplayName, "Dialect display name may not be null");
        checkArgument(isValidIdentifier(identifier), "Invalid language identifier: %s", identifier);
        checkArgument(isValidIdentifier(dialectIdentifier), "Invalid dialect language identifier: %s", dialectIdentifier);

        String key = identifier.toLowerCase() + "__" + dialectIdentifier;
        dialectCache.putIfAbsent(key, new Language(identifier.toLowerCase(), displayName, dialectIdentifier, dialectDisplayName));
        return dialectCache.get(key);
    }

    /**
     * Returns the dialect's identifier of this language.
     *
     * @return the dialect's identifier of this language.
     */
    @Nullable
    public String getDialect() {
        return dialect;
    }

    /**
     * Returns the dialect's display name of this language (without main language). This is the value that should be
     * displayed to a human. To get also the display name of the main you have to call {@link #getDisplayName()}.
     *
     * @return the dialect's display name of this language.
     */
    @Nullable
    public String getDialectDisplayName() {
        return dialectDisplayName;
    }

    /**
     * Returns the display name of this language (without the dialect). This is the value that should be displayed to a
     * human user.
     *
     * @return the display name of this language.
     */
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the identifier of this language (without the dialect). This method has to be unique per runtime and
     * should only be used internally.
     *
     * @return the identifier of this language.
     */
    @NotNull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("identifier", identifier)
                .add("dialect", dialect)
                .toString();
    }
}
