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
 * The {@link UnsupportedDictionaryException} should always be thrown if an engine is queried with an unsupported type
 * of dictionary.
 */
public class UnsupportedDictionaryException extends Exception {

    private static final long serialVersionUID = -5635269869139884252L;

    private final Language inLanguage;

    private final Language outLanguage;

    private final boolean allowBothWay;

    public UnsupportedDictionaryException(Language inLanguage, Language outLanguage, boolean allowBothWay) {
        super("Unsupported bilingual query configuration: in[" + inLanguage + "], out[" + outLanguage + "]" + ", allowBothWay[" + allowBothWay + "]");
        this.inLanguage = inLanguage;
        this.outLanguage = outLanguage;
        this.allowBothWay = allowBothWay;
    }

    public Language getInLanguage() {
        return inLanguage;
    }

    public Language getOutLanguage() {
        return outLanguage;
    }

    public boolean isAllowBothWay() {
        return allowBothWay;
    }
}
