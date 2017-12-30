/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
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

package org.xlrnet.metadict.core.services.aggregation.merge;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.language.GrammaticalForm;
import org.xlrnet.metadict.api.query.DictionaryObject;
import org.xlrnet.metadict.api.query.DictionaryObjectBuilder;
import org.xlrnet.metadict.api.query.ImmutableDictionaryObject;
import org.xlrnet.metadict.core.util.CollectionUtils;
import org.xlrnet.metadict.core.util.CommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Common utility functions for merging {@link org.xlrnet.metadict.api.query.DictionaryObject} objects.
 */
public class DictionaryObjectMergeUtil {

    private static final String JOINED_ATTRIBUTES_SEPARATOR = ", ";

    private DictionaryObjectMergeUtil() {

    }

    /**
     * <p>Merges the {@link DictionaryObject} objects in a given list:<br/> (If more than two objects are being compared
     * to each other, use the most often used)</p> <ol> <li>If the grammatical gender is missing, use the grammatical
     * gender of the other entry. Abort merging if they differ.</li> <li>Merge both descriptions, domain and
     * abbreviation, unless they are equal when normalized (i.e. stripped and lowercased). If they are equal, use the
     * first found.</li> <li>Normalize all additional forms and compare them. If any is missing, add them. If the forms
     * are unequal when normalized, merge them.</li> <li>Merge alternate forms and meanings.</li> <li>Use the most-often
     * occurred pronunciation and syllabification or first non-empty if none is most often.</li </ol>
     *
     * @param sourceObjects
     *         objects to merge
     * @return A single merged object.
     */
    @NotNull
    static DictionaryObject mergeDictionaryObjects(@NotNull List<DictionaryObject> sourceObjects) {
        DictionaryObjectBuilder builder = ImmutableDictionaryObject.builder();
        builder.setLanguage(sourceObjects.get(0).getLanguage());
        builder.setGeneralForm(sourceObjects.get(0).getGeneralForm());

        builder.setGrammaticalGender(CommonUtils.getFirstNotNull(sourceObjects, DictionaryObject::getGrammaticalGender));

        builder.setDescription(mergeAttribute(sourceObjects, DictionaryObject::getDescription));
        builder.setDomain(mergeAttribute(sourceObjects, DictionaryObject::getDomain));
        builder.setAbbreviation(mergeAttribute(sourceObjects, DictionaryObject::getAbbreviation));
        builder.setPronunciation(mergeAttribute(sourceObjects, DictionaryObject::getPronunciation));
        builder.setMeanings(CollectionUtils.divideAndFilterNormalized(sourceObjects, DictionaryObject::getMeanings, CommonUtils::stripAndLowercase));
        builder.setAlternateForms(CollectionUtils.divideAndFilterNormalized(sourceObjects, DictionaryObject::getAlternateForms, CommonUtils::stripAndLowercase));

        Map<GrammaticalForm, String> mergedAdditionalForms = CollectionUtils.divideAndMerge(
                sourceObjects,
                DictionaryObject::getAdditionalForms,
                CommonUtils::stripAndLowercase,
                ((v, r) -> StringUtils.stripToEmpty(v) + (r != null ? ", " + r : ""))
        );
        builder.setAdditionalForms(mergedAdditionalForms);
        setFirstNonEmptySyllabificationInBuilder(sourceObjects, builder);

        return builder.build();
    }

    @Nullable
    static private String mergeAttribute(@NotNull List<DictionaryObject> sourceObjects, @NotNull Function<DictionaryObject, String> collector) {
        Map<String, String> normalizedAndActualAttributes = new HashMap<>(sourceObjects.size());
        for (DictionaryObject sourceObject : sourceObjects) {
            String collectedString = collector.apply(sourceObject);
            if (collectedString == null) {
                continue;
            }
            String normalized = CommonUtils.stripAndLowercase(collectedString);
            normalizedAndActualAttributes.putIfAbsent(normalized, collectedString);
        }

        String joinedAttributes = StringUtils.join(normalizedAndActualAttributes.values(), JOINED_ATTRIBUTES_SEPARATOR);
        return StringUtils.stripToNull(joinedAttributes);
    }

    private static void setFirstNonEmptySyllabificationInBuilder(@NotNull List<DictionaryObject> sourceObjects, @NotNull DictionaryObjectBuilder builder) {
        for (DictionaryObject sourceObject : sourceObjects) {
            List<String> syllabification = sourceObject.getSyllabification();
            if (!syllabification.isEmpty()) {
                builder.setSyllabification(syllabification);
                break;
            }
        }
    }

}
