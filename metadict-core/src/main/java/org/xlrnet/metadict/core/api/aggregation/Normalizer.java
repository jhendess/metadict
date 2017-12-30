package org.xlrnet.metadict.core.api.aggregation;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.EntryType;

/**
 * Normalizer for a given input string based on language and entry type dependent rules. Normalized
 * forms should be easily identifiable by their grammatical properties without knowing them.
 * E.g.: while "question" may be both a English noun or verb, the corresponding English verb normalizer should transform
 * it to "to question".
 */
public interface Normalizer {

    /**
     * Normalies the given input string. All input can be expected to be in lower-case and trimmed of whitespace.
     *
     * @param input
     *         The input string to normalize.
     * @return The normalized input string.
     */
    @NotNull
    String normalize(@NotNull String input);

    /**
     * Returns the language that can be normalized by the normalizer.
     */
    @NotNull
    Language getLanguage();

    /**
     * Returns the entry types that can normalized by the normalizer.
     */
    @NotNull
    EntryType getEntryType();

}
