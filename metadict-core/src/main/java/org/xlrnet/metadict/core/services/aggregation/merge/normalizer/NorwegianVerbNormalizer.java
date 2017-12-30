package org.xlrnet.metadict.core.services.aggregation.merge.normalizer;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.EntryType;
import org.xlrnet.metadict.core.api.aggregation.Normalizer;

/**
 * Normalizer for English verbs. Adds a "to" if in front of the verb if it doesn't exist yet.
 */
public class NorwegianVerbNormalizer implements Normalizer {

    private static final String VERB_PREFIX = "å ";

    @NotNull
    @Override
    public String normalize(@NotNull String input) {
        return StringUtils.prependIfMissing(input, VERB_PREFIX);
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return Language.NORWEGIAN;
    }

    @NotNull
    @Override
    public EntryType getEntryType() {
        return EntryType.VERB;
    }
}
