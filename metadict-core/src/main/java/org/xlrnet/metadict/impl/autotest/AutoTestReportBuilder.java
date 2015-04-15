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

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for {@link AutoTestReport} objects.
 */
public class AutoTestReportBuilder {

    private List<AutoTestResult> testResultList = new ArrayList<>();

    private int failedTests = 0;

    private int successfulTests = 0;

    private int totalTests = 0;

    AutoTestReportBuilder addAutoTestResult(@NotNull AutoTestResult autoTestResult) {
        checkNotNull(autoTestResult);

        if (autoTestResult.wasSuccessful())
            successfulTests++;
        else
            failedTests++;

        totalTests++;

        testResultList.add(autoTestResult);
        return this;
    }

    AutoTestReport build() {
        return new AutoTestReportImpl(testResultList, successfulTests, failedTests, totalTests);
    }
}
