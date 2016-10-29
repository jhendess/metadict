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

package org.xlrnet.metadict.api.storage;

import com.google.common.base.MoreObjects;
import org.xlrnet.metadict.api.exception.MetadictTechnicalException;

/**
 * Thrown if an access operation encountered an unexpected state that indicates no backend failure. This includes the
 * following concrete states:
 * <p>
 * <ul> <li>Trying to create a new value under an already existing key</li> <li>Trying to update a value under a
 * non-existing key</li> </ul>
 */
public class StorageOperationException extends MetadictTechnicalException {

    private static final long serialVersionUID = 3229352284448948928L;

    private String namespace;

    private String key;

    public StorageOperationException(String message, String namespace, String key) {
        super(message);
        this.namespace = namespace;
        this.key = key;
    }

    public StorageOperationException(String message, String namespace, String key, Throwable throwable) {
        super(message, throwable);
        this.namespace = namespace;
        this.key = key;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("namespace", this.namespace)
                .add("key", this.key)
                .toString();
    }
}
