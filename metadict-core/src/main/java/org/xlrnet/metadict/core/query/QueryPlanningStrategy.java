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

package org.xlrnet.metadict.core.query;

import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.core.main.EngineRegistry;

/**
 * The {@link QueryPlanningStrategy} interface is used to implement various strategies for selecting the engines that
 * should
 * be used for executing a {@link QueryRequest}.
 */
public interface QueryPlanningStrategy {

    /**
     * Calculate a query plan for the given {@link QueryRequest}. The provided {@link EngineRegistry} should be used
     * for accessing the available implementations of {@link org.xlrnet.metadict.api.engine.SearchEngine}.
     *
     * @param queryRequest
     *         The query request for which a query plan has to be calculated.
     * @param engineRegistry
     *         The registry where all available engines are registered.
     * @return an executable {@link QueryPlan}.
     */
    @NotNull
    QueryPlan calculateQueryPlan(@NotNull QueryRequest queryRequest, @NotNull EngineRegistry engineRegistry);

}
