package org.xlrnet.metadict.core.services.aggregation.merge.normalizer;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.EntryType;
import org.xlrnet.metadict.core.api.aggregation.Normalizer;

/**
 * Normalizer for English verbs. Adds a "to" if in front of the verb if it doesn't exist yet.
 */
public class EnglishVerbNormalizer implements Normalizer {

    private static final String VERB_PREFIX = "to ";

    @NotNull
    @Override
    public String normalize(@NotNull String input) {
        return StringUtils.prependIfMissing(input, VERB_PREFIX);
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return Language.ENGLISH;
    }

    @NotNull
    @Override
    public EntryType getEntryType() {
        return EntryType.VERB;
    }
}
