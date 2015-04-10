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

/**
 * This enumeration represents a grammatical case. Some languages like German or Latin use a rich case system that can
 * be represented using this predefined enumeration.
 */
public enum GrammaticalCase implements GrammaticalForm {

    NOMINATIVE,

    GENITIVE,

    DATIVE,

    ACCUSATIVE,

    INDEFINITE_FORM,

    DEFINITE_FORM;

    /**
     * Return the identifier for this grammatical form. The identifier should be as unique as possible and written in
     * lowercase letters. When implementing this interface inside an {@link Enum}, this method should return the enum
     * value in lowercase (i.e. {@link Enum#name()} must be lowercased).
     *
     * @return the identifier for this grammatical form.
     */
    @Override
    public String getFormIdentifier() {
        return name().toLowerCase();
    }
}
