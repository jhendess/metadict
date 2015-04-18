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

package org.xlrnet.metadict.impl.autotest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.engine.AutoTestCase;
import org.xlrnet.metadict.api.query.EngineQueryResult;
import org.xlrnet.metadict.impl.core.EngineRegistry;

import java.util.Optional;

/**
 * The {@link AutoTestResult} provides information about the test result of a single {@link
 * org.xlrnet.metadict.api.engine.AutoTestCase}.
 */
public class AutoTestResult {

    private final Optional<EngineQueryResult> actualEngineQueryResult;

    private final String canonicalEngineName;

    private final long executionTime;

    private final AutoTestCase testCase;

    private final Optional<Exception> thrownException;

    private final boolean isSuccessful;

    private AutoTestResult(@NotNull String canonicalEngineName, long executionTime, @NotNull AutoTestCase testCase, @NotNull EngineQueryResult actualEngineQueryResult) {
        this.actualEngineQueryResult = Optional.of(actualEngineQueryResult);
        this.canonicalEngineName = canonicalEngineName;
        this.executionTime = executionTime;
        this.testCase = testCase;
        this.thrownException = Optional.empty();
        this.isSuccessful = true;
    }

    private AutoTestResult(@NotNull String canonicalEngineName, long executionTime, @NotNull AutoTestCase testCase, @Nullable Exception thrownException, @Nullable EngineQueryResult actualEngineQueryResult) {
        this.actualEngineQueryResult = Optional.ofNullable(actualEngineQueryResult);
        this.canonicalEngineName = canonicalEngineName;
        this.executionTime = executionTime;
        this.testCase = testCase;
        this.thrownException = Optional.ofNullable(thrownException);
        this.isSuccessful = false;
    }

    /**
     * Creates a new auto test result object for a failed auto test.
     *
     * @param canonicalEngineName
     *         Canonical name of the engine.
     * @param executionTime
     *         Internal execution time for the query.
     * @param testCase
     *         The original test case object.
     * @param thrownException
     *         The exception that was thrown during execution.
     * @param actualEngineQueryResult
     *         The actual query result that was returned from the queried engine, if any available.
     * @return a new auto test result object for a failed auto test.
     */
    public static AutoTestResult failed(@NotNull String canonicalEngineName, long executionTime, @NotNull AutoTestCase testCase, @Nullable Exception thrownException, @Nullable EngineQueryResult actualEngineQueryResult) {
        return new AutoTestResult(canonicalEngineName, executionTime, testCase, thrownException, actualEngineQueryResult);
    }

    /**
     * Creates a new auto test result object for a successful auto test.
     *
     * @param canonicalEngineName
     *         Canonical name of the engine.
     * @param executionTime
     *         Internal execution time for the query.
     * @param testCase
     *         The original test case object.
     * @param actualEngineQueryResult
     *         The actual query result that was returned from the queried engine.
     * @return a new auto test result object for a successful auto test.
     */
    public static AutoTestResult succeeded(@NotNull String canonicalEngineName, long executionTime, @NotNull AutoTestCase testCase, @NotNull EngineQueryResult actualEngineQueryResult) {
        return new AutoTestResult(canonicalEngineName, executionTime, testCase, actualEngineQueryResult);
    }

    /**
     * Returns an {@link Optional} with the actual {@link EngineQueryResult} object that was returned by the
     * engine. If any exception was thrown during the execution, there won't be any query result and the
     * {@link Optional} won't contain any object. In this case {@link #getThrownException()} should return the thrown
     * exception.
     *
     * @return an optional with the actual engine query result if any was provided.
     */
    @NotNull
    public Optional<EngineQueryResult> getActualEngineQueryResult() {
        return this.actualEngineQueryResult;
    }

    /**
     * Returns the canonical class name of the tested search engine. A canonical includes always the full package name
     * and the name of the class. This name must be the same under which the engine was registered in the {@link
     * EngineRegistry} during startup.
     * <p>
     * Example:
     * A class DummyEngine in package com.example.metadict should have the canonical name
     * {@code com.example.metadict.DummyEngine}.
     *
     * @return the canonical class name of the tested search engine.
     */
    @NotNull
    public String getCanonicalEngineName() {
        return this.canonicalEngineName;
    }

    /**
     * Returns the duration in milliseconds that the test case took for execution.
     *
     * @return the duration in milliseconds that the test case took for execution.
     */
    public long getExecutionTime() {
        return this.executionTime;
    }

    /**
     * Returns the original {@link AutoTestCase} object that described the executed test case. This includes the query
     * configuration including dictionaries and the expected result set.
     *
     * @return the original {@link AutoTestCase} object that described the executed test case.
     */
    @NotNull
    public AutoTestCase getTestCase() {
        return this.testCase;
    }

    /**
     * Returns an optional exception that might have been thrown during the execution of the test case. If no exception
     * was thrown, the returned {@link Optional} contains no object. However, if any exception was thrown during the
     * query, {@link #getActualEngineQueryResult()} won't contain a object.
     *
     * @return an optional exception that might have been thrown during the execution of the test case.
     */
    @NotNull
    public Optional<Exception> getThrownException() {
        return this.thrownException;
    }

    /**
     * Returns if the test was overall successful. A test case is successful if all expected results could be found in
     * the actual result set.
     *
     * @return true if the test was successful, false otherwise.
     */
    public boolean isSuccessful() {
        return this.isSuccessful;
    }

}
