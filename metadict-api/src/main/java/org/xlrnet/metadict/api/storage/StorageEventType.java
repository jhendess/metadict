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

import org.xlrnet.metadict.api.event.MetadictEventType;

import java.util.Map;

/**
 * Different events a {@link StorageEventListener} may listen to.
 */
public enum StorageEventType implements MetadictEventType {

    /**
     * Event that will be fired after the storage engine was created. All listeners will be notified after {@link
     * StorageServiceProvider#createNewStorageService(Map)} has been called but before the storage has been injected.
     */
    POST_CREATE(false),

    /**
     * Event that will be fired after the storage engine was injected into a new consumer. All listeners will be
     * notified after the {@link #POST_CREATE} event has occurred but before the storage has been injected into the new
     * consumer.
     */
    POST_INJECT(false),

    /**
     * Event that will be fired just before the storage engine is about to be shut down. All listeners will be notified
     * before {@link StorageEngine#shutdown()} has been called.
     */
    PRE_SHUTDOWN(false),

    /**
     * Event that will be fired after the storage engine has been shut down. All listeners will be notified after {@link
     * StorageEngine#shutdown()} has been called.
     */
    POST_SHUTDOWN(false),

    /**
     * Periodic event that will be fired by the core in the defined interval after the engine has been created and
     * before the shutdown. This event will be fired for the first time when the defined interval has passed.
     */
    MAINTENANCE(true);

    boolean periodic;

    StorageEventType(boolean periodic) {
        this.periodic = periodic;
    }

    @Override
    public boolean isPeriodic() {
        return periodic;
    }

    @Override
    public String getName() {
        return this.name();
    }
}
