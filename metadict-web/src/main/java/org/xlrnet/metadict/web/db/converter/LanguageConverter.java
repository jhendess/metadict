package org.xlrnet.metadict.web.db.converter;

import org.xlrnet.metadict.api.language.Language;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA converter which converts {@link org.xlrnet.metadict.api.language.Language} objects to string and vice-versa.
 */
@Converter
public class LanguageConverter implements AttributeConverter<Language, String>{

    @Override
    public String convertToDatabaseColumn(Language language) {
        return language.getIdentifier();
    }

    @Override
    public Language convertToEntityAttribute(String dbData) {
        return Language.getLanguageById(dbData);
    }
}
