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

package org.xlrnet.metadict.core.services.autotest;

import java.util.List;

/**
 * The {@link AutoTestReport} serves as the main container for multiple {@link AutoTestResult}. It is used to describe
 * a full report of multiple test cases and contains
 */
public interface AutoTestReport extends Iterable<AutoTestResult> {

    /**
     * Returns the amount of failed test cases in this report i.e. the number of objects where {@link
     * AutoTestResult#isSuccessful()} returns false.
     *
     * @return the amount of failed test cases in this report.
     */
    int getFailedTests();

    /**
     * Returns the amount of successfully executed test cases in this report i.e. the number of objects where {@link
     * AutoTestResult#isSuccessful()} returns true.
     *
     * @return the amount of successfully executed test cases in this report.
     */
    int getSuccessfulTests();

    List<AutoTestResult> getTestResults();

    /**
     * Returns the amount of total test cases in this report.
     *
     * @return the amount of total test cases in this report.
     */
    int getTotalTestCount();

}
