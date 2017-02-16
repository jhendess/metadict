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

package org.xlrnet.metadict.api.language;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Special case of a {@link GrammaticalForm} for nouns. Such a form may contain a {@link GrammaticalGender},
 * {@link GrammaticalNumber} and {@link GrammaticalCase}.
 */
public class NounForm implements GrammaticalForm, Serializable {

    private final GrammaticalGender gender;

    private final GrammaticalNumber number;

    private final GrammaticalCase grammaticalCase;

    public NounForm(@Nullable GrammaticalCase grammaticalCase, @Nullable GrammaticalNumber number, @Nullable GrammaticalGender gender) {
        checkArgument((grammaticalCase != null) || (number != null) || (gender != null), "At least one of the grammatical case, number or gender must be not null");
        this.gender = gender;
        this.number = number;
        this.grammaticalCase = grammaticalCase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NounForm nounForm = (NounForm) o;
        return this.gender == nounForm.gender &&
                this.number == nounForm.number &&
                this.grammaticalCase == nounForm.grammaticalCase;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gender, this.number, this.grammaticalCase);
    }

    @NotNull
    @Override
    public String getFormIdentifier() {
        String form = (this.grammaticalCase != null ? this.grammaticalCase.getFormIdentifier() + " " : null) + (this.number != null ? this.number.getFormIdentifier() + " " : null) + (this.gender != null ? this.gender.getFormIdentifier() : null);
        return StringUtils.stripToEmpty(form);
    }

    public String toString() {
        return getFormIdentifier();
    }
}
