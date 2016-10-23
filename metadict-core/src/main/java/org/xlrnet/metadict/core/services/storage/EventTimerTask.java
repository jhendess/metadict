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

package org.xlrnet.metadict.core.services.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.event.MetadictEventListener;
import org.xlrnet.metadict.api.event.MetadictEventType;

import java.util.TimerTask;

/**
 * Simple {@link TimerTask} implementation for firing events to a defined listener.
 */
public class EventTimerTask<T> extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventTimerTask.class);

    private final MetadictEventType eventType;

    private final MetadictEventListener<T> eventListener;

    private final T eventObject;

    public EventTimerTask(MetadictEventType eventType, MetadictEventListener<T> eventListener, T eventObject) {
        this.eventType = eventType;
        this.eventListener = eventListener;
        this.eventObject = eventObject;
    }

    @Override
    public void run() {
        LOGGER.debug("Firing '{}' event to '{}'", this.eventType, this.eventListener.getClass().getCanonicalName());
        try {
            this.eventListener.handleEvent(this.eventObject);
        } catch (Exception e) {
            LOGGER.error("Event handler threw an exception", e);
        }
    }

}
