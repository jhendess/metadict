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

package org.xlrnet.metadict.impl.query;

import org.xlrnet.metadict.api.query.EngineQueryResult;

/**
 * Implementation for {@link QueryStepResult}.
 */
public class QueryStepResultImpl implements QueryStepResult {

    private final QueryStep queryStep;

    private final EngineQueryResult engineQueryResult;

    private final long executionTime;

    private final boolean failedStep;

    private final String errorMessage;

    QueryStepResultImpl(QueryStep queryStep, EngineQueryResult engineQueryResult, long executionTime, boolean failedStep, String errorMessage) {
        this.queryStep = queryStep;
        this.engineQueryResult = engineQueryResult;
        this.executionTime = executionTime;
        this.failedStep = failedStep;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the result of the attached {@link QueryStep}.
     *
     * @return the result of the attached {@link QueryStep}.
     */
    @Override
    public EngineQueryResult getEngineQueryResult() {
        return engineQueryResult;
    }

    /**
     * Returns the error message of the exception that cancelled the query execution. This should return a non-null
     * value when {@link #isFailedStep()} returns true.
     *
     * @return the error message of the exception that cancelled the query execution.
     */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns the time that the attached {@link QueryStep} took in milliseconds.
     *
     * @return the time that the attached {@link QueryStep} took in milliseconds.
     */
    @Override
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * Returns the {@link QueryStep} that was executed.
     *
     * @return the {@link QueryStep} that was executed.
     */
    @Override
    public QueryStep getQueryStep() {
        return queryStep;
    }

    /**
     * Returns true, if the attached {@link QueryStep} has failed. If this message returns true, then {@link
     * #getErrorMessage()} should return the message of the thrown exception.
     *
     * @return true, if the attached {@link QueryStep} has failed.
     */
    @Override
    public boolean isFailedStep() {
        return failedStep;
    }
}
