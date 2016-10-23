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

import java.util.Iterator;
import java.util.List;

/**
 * Implementation of {@link AutoTestReport}.
 */
public class ImmutableAutoTestReport implements AutoTestReport {

    private final int failedTests;

    private final int successfulTests;

    private final int totalTestCount;

    private final List<AutoTestResult> testResultList;

    ImmutableAutoTestReport(List<AutoTestResult> testResultList, int successfulTests, int failedTests, int totalTestCount) {
        this.failedTests = failedTests;
        this.totalTestCount = totalTestCount;
        this.successfulTests = successfulTests;
        this.testResultList = testResultList;
    }

    @Override
    public int getFailedTests() {
        return this.failedTests;
    }

    @Override
    public int getSuccessfulTests() {
        return this.successfulTests;
    }

    @Override
    public List<AutoTestResult> getTestResults() {
        return this.testResultList;
    }

    @Override
    public int getTotalTestCount() {
        return this.totalTestCount;
    }

    @Override
    public Iterator<AutoTestResult> iterator() {
        return this.testResultList.iterator();
    }
}
