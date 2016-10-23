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

package org.xlrnet.metadict.core.services.aggregation;

import org.xlrnet.metadict.core.api.aggegation.OrderStrategy;
import org.xlrnet.metadict.core.api.aggegation.ResultGroup;

/**
 * Use the {@link OrderType} to determine how the entries in each {@link ResultGroup} should be ordered.
 */
public enum OrderType {

    /** Order entries based on their Levensthein distance from the original query. */
    RELEVANCE(new LevenstheinRelevanceOrderStrategy()),

    /** Just pass through the order as given by the engines. */
    PASSTHROUGH(new PassthroughOrderStrategy());

    private OrderStrategy orderStrategy;

    OrderType(OrderStrategy orderStrategy) {
        this.orderStrategy = orderStrategy;
    }

    public OrderStrategy getOrderStrategy() {
        return this.orderStrategy;
    }

}
