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

import org.xlrnet.metadict.api.query.BilingualQueryResult;
import org.xlrnet.metadict.api.query.EngineQueryResult;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for {@link QueryStepResult} objects.
 */
public class QueryStepResultBuilder {

    private AbstractQueryStep queryStep;

    private EngineQueryResult engineQueryResult;

    private long executionTime;

    private boolean failedStep = false;

    private String errorMessage;

    /**
     * Returns a new instance of {@link QueryStepResult}. This method will throw a {@link NullPointerException} if
     * either the {@link BilingualQueryStep} or the {@link BilingualQueryResult} is not set.
     *
     * @return a new object of {@link QueryStepResult}
     */
    public QueryStepResult build() throws NullPointerException {
        checkNotNull(queryStep, "Provided query step may not be null");
        checkNotNull(engineQueryResult, "Provided query result may not be null");

        return new ImmutableQueryStepResult(queryStep, engineQueryResult, executionTime, failedStep, errorMessage);
    }

    /**
     * Set the result of the attached query step.
     *
     * @param engineQueryResult
     *         The result of the attached query step.
     * @return the current builder
     */
    public QueryStepResultBuilder setEngineQueryResult(EngineQueryResult engineQueryResult) {
        this.engineQueryResult = engineQueryResult;
        return this;
    }

    /**
     * Returns the error message of the exception that cancelled the query execution. This should return a non-null
     * value when {@link #setFailedStep(boolean)}} is set to true.
     *
     * @param errorMessage
     *         The error message of the exception that cancelled the query execution.
     * @return the current builder
     */
    public QueryStepResultBuilder setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    /**
     * Set the time that the attached {@link BilingualQueryStep} took in milliseconds.
     *
     * @param executionTime
     *         The time that the attached {@link BilingualQueryStep} took in milliseconds.
     * @return the current builder
     */
    public QueryStepResultBuilder setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
        return this;
    }

    /**
     * Should be set to true, if the attached {@link BilingualQueryStep} has failed. If this is set to true, then {@link
     * #setErrorMessage(String)} should be set with the message of the catched Exception.
     *
     * @param failedStep
     *         True, if the attached {@link BilingualQueryStep} has failed.
     * @return the current builder
     */
    public QueryStepResultBuilder setFailedStep(boolean failedStep) {
        this.failedStep = failedStep;
        return this;
    }

    /**
     * Set the {@link BilingualQueryStep} that was executed.
     *
     * @param queryStep
     *         The {@link BilingualQueryStep} that was executed.
     * @return the current builder
     */
    public QueryStepResultBuilder setQueryStep(AbstractQueryStep queryStep) {
        this.queryStep = queryStep;
        return this;
    }
}
