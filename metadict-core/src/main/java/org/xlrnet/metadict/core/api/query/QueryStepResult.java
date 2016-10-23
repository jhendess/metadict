/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jakob Hendeß
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

package org.xlrnet.metadict.core.api.query;

import org.xlrnet.metadict.api.query.EngineQueryResult;
import org.xlrnet.metadict.core.services.query.AbstractQueryStep;

import java.io.Serializable;

/**
 * The {@link QueryStepResult} represents the result of a single {@link AbstractQueryStep}. It contains both the
 * executed query step, the {@link QueryStepResult} object with the results from the query step and additional
 * performance metrics.
 * <p>
 * Note, that this interface returns only abstract objects - each has to be casted to the according monolingual or
 * bilingual implementation!
 */
public interface QueryStepResult extends Serializable {

    /**
     * Returns the result of the attached {@link AbstractQueryStep}.
     *
     * @return the result of the attached {@link AbstractQueryStep}.
     */
    EngineQueryResult getEngineQueryResult();

    /**
     * Returns the error message of the exception that cancelled the query execution. This should return a non-null
     * value when {@link #isFailedStep()} returns true.
     *
     * @return the error message of the exception that cancelled the query execution.
     */
    String getErrorMessage();

    /**
     * Returns the time that the attached {@link AbstractQueryStep} took in milliseconds.
     *
     * @return the time that the attached {@link AbstractQueryStep} took in milliseconds.
     */
    long getExecutionTime();

    /**
     * Returns the {@link AbstractQueryStep} that was executed. Make sure, to cast the returned object accordingly!
     *
     * @return the {@link AbstractQueryStep} that was executed.
     */
    AbstractQueryStep getQueryStep();

    /**
     * Returns true, if the attached {@link AbstractQueryStep} has failed. If this message returns true, then {@link
     * #getErrorMessage()} should return the message of the thrown exception.
     *
     * @return true, if the attached {@link AbstractQueryStep} has failed.
     */
    boolean isFailedStep();
}
