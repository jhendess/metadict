package org.xlrnet.metadict.core.services.aggregation.merge;

import com.google.common.base.Objects;
import org.apache.commons.lang3.tuple.Pair;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.EntryType;

/**
 * Internal identifier class for a potential merge candidate.
 */
public class MergeCandidateIdentifier {

    private final Pair<Language, Language> languagePair;

    private final EntryType entryType;

    private final Pair<String, String> generalForms;

    public MergeCandidateIdentifier(Pair<Language, Language> languagePair, EntryType entryType, Pair<String, String> generalForms) {
        this.languagePair = languagePair;
        this.entryType = entryType;
        this.generalForms = generalForms;
    }

    public Pair<Language, Language> getLanguagePair() {
        return languagePair;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public Pair<String, String> getGeneralForms() {
        return generalForms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MergeCandidateIdentifier)) return false;
        MergeCandidateIdentifier that = (MergeCandidateIdentifier) o;
        return Objects.equal(languagePair, that.languagePair) &&
                entryType == that.entryType &&
                Objects.equal(generalForms, that.generalForms);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(languagePair, entryType, generalForms);
    }
}
