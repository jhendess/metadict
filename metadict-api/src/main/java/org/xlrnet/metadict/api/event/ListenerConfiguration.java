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

package org.xlrnet.metadict.api.event;

import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Generic configuration container for {@link MetadictEventListener}. Each listener must be wrapped inside this
 * container for correct detection by the core.
 */
public class ListenerConfiguration<T extends MetadictEventType, L extends MetadictEventListener> {

    final long intervalSeconds;

    final private T eventType;

    final private L eventListener;

    protected ListenerConfiguration(T eventType, L eventListener, long period) {
        this.eventType = eventType;
        this.eventListener = eventListener;
        this.intervalSeconds = period;
    }

    /**
     * Creates a new listener configuration for a given event type. The core will initialize this configuration and call
     * the appropriate listener in case of the expected event.
     *
     * @param eventType
     *         The type of event that should be listened to.
     * @param eventListener
     *         The listener that will be invoked upon entry of the event.
     * @return A new listener configuration.
     */
    @NotNull
    public static <T extends MetadictEventType, L extends MetadictEventListener> ListenerConfiguration<T, L> newConfiguration(@NotNull T eventType, @NotNull L eventListener) {
        return new ListenerConfiguration<>(eventType, eventListener, 0);
    }

    /**
     * Creates a new periodic listener configuration for a given event type. The core will initialize this configuration
     * and call the appropriate listener in a given interval under the conditions of the supplied event type.
     * <p>
     * Note: it is recommended to use the methods of an appropriate builder that creates {@link Listenable} objects
     * instead of calling this method manually to avoid confusion.
     *
     * @param eventType
     *         The type of event that should be listened to.
     * @param eventListener
     *         The listener that will be invoked upon entry of the event.
     * @param intervalSeconds
     *         The interval in seconds where the listener will be called. Must be at least 30.
     * @return A new listener configuration.
     */
    @NotNull
    public static <T extends MetadictEventType, L extends MetadictEventListener> ListenerConfiguration<T, L> newPeriodicConfiguration(@NotNull T eventType, @NotNull L eventListener, long intervalSeconds) {
        checkArgument(eventType.isPeriodic(), "Supplied event type must be periodic");
        checkArgument(intervalSeconds >= 30, "Interval must be greater or equal to 30");
        return new ListenerConfiguration<>(eventType, eventListener, intervalSeconds);
    }

    public T getEventType() {
        return eventType;
    }

    public L getEventListener() {
        return eventListener;
    }

    public long getIntervalSeconds() {
        return intervalSeconds;
    }
}
