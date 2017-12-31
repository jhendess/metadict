package org.xlrnet.metadict.core.services.aggregation.merge;

import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.exception.ConfigurationException;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.EntryType;
import org.xlrnet.metadict.core.api.aggregation.Normalizer;
import org.xlrnet.metadict.core.util.CommonUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service which can be used for normalization of {@link org.xlrnet.metadict.api.query.DictionaryObject}.
 */
@Singleton
public class NormalizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NormalizationService.class);

    /**
     * Raw list of injected normalizers.
     */
    private final Set<Normalizer> normalizers;

    /**
     * Indexed map of normalizers.
     */
    private final MultiKeyMap normalizerMap = new MultiKeyMap();

    @Inject
    public NormalizationService(Set<Normalizer> normalizers) {
        this.normalizers = normalizers;
    }

    @PostConstruct
    void initialize() {
        for (Normalizer normalizer : normalizers) {
            registerNormalizer(normalizer);
        }
    }

    private void registerNormalizer(Normalizer normalizer) {
        Language language = normalizer.getLanguage();
        EntryType entryType = normalizer.getEntryType();

        checkNotNull(language, "Normalizer language may not be null");
        checkNotNull(entryType, "Supported entry type of normalizer may not be null");

        if (normalizerMap.containsKey(language, entryType)) {
            throw new ConfigurationException(String.format("Duplicate normalizer for %s %s", language, entryType));
        }
        String identifier = language.getIdentifierWithDialect();
        normalizerMap.put(identifier, entryType, normalizer);

        LOGGER.debug("Registered {} as normalizer for language {} and type {}", normalizer.getClass().getCanonicalName(), language.getIdentifierWithDialect(), entryType);
    }

    /**
     * Normalizes the general form of the given dictionary object using language and entry-type dependent patterns.
     *
     * @param dictionaryObject
     *         The dictionary object to normalize.
     * @param type
     *         The entry type used for normalization.
     * @return The normalized general form.
     */
    public String getNormalizedGeneralForm(DictionaryObject dictionaryObject, EntryType type) {
        String generalForm = dictionaryObject.getGeneralForm();
        String normalized = CommonUtils.stripAndLowercase(generalForm);
        Normalizer normalizer = getNormalizerByLanguageAndEntryType(dictionaryObject.getLanguage(), type);
        if (normalizer != null) {
            normalized = normalizer.normalize(normalized);
        }
        return normalized;
    }

    private Normalizer getNormalizerByLanguageAndEntryType(Language language, EntryType type) {
        Object normalizer = normalizerMap.get(language.getIdentifierWithDialect(), type);
        if (language.isDialect() && normalizer == null) {
            normalizer = normalizerMap.get(language.getIdentifier(), type);
        }
        return (Normalizer) normalizer;
    }
}
