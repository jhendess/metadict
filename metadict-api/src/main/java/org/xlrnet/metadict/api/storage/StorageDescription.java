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

import org.xlrnet.metadict.api.event.Listenable;
import org.xlrnet.metadict.api.metadata.BackendDescription;

/**
 * The interface {@link StorageDescription} is used to describe storage engine in a human readable format. Therefore the
 * description contains information like e.g. the name of the engine, the author and copyright information.
 * <p>
 * A {@link StorageDescription} can also provide a set of different listeners that can be registered on the core and
 * will be called on certain events.
 * <p>
 * New instances can be created by using the {@link StorageDescriptionBuilder}.
 */
public interface StorageDescription extends BackendDescription, Listenable<StorageEventType, StorageEventListener> {

}
