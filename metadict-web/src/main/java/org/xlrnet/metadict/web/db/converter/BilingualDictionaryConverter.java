package org.xlrnet.metadict.web.db.converter;

import org.xlrnet.metadict.api.language.BilingualDictionary;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA converter which converts {@link org.xlrnet.metadict.api.language.BilingualDictionary} to String and vice-versa.
 */
@Converter
public class BilingualDictionaryConverter implements AttributeConverter<BilingualDictionary, String> {

    @Override
    public String convertToDatabaseColumn(BilingualDictionary attribute) {
        return attribute.getQueryStringWithDialect();
    }

    @Override
    public BilingualDictionary convertToEntityAttribute(String dbData) {
        return BilingualDictionary.fromQueryString(dbData);
    }
}
