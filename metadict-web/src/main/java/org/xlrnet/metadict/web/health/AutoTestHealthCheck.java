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

package org.xlrnet.metadict.web.health;

import org.xlrnet.metadict.core.autotest.AutoTestManager;
import org.xlrnet.metadict.core.autotest.AutoTestReport;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

import javax.inject.Inject;

/**
 * Health check for running the internal auto tests from metadict. This component should not be called too often to
 * avoid unnecessarily high loads on backend systems.
 */
public class AutoTestHealthCheck extends NamedHealthCheck {

    private final AutoTestManager autoTestManager;

    @Inject
    AutoTestHealthCheck(AutoTestManager autoTestManager) {
        this.autoTestManager = autoTestManager;
    }

    @Override
    public String getName() {
        return "autotest";
    }

    @Override
    protected Result check() throws Exception {
        AutoTestReport autoTestResults = this.autoTestManager.runAllRegisteredAutoTests();
        if (autoTestResults.getFailedTests() == 0) {
            return Result.healthy();
        } else {
            return Result.unhealthy("%d/%d auto test cases failed", autoTestResults.getFailedTests(), autoTestResults.getTotalTestCount());
        }
    }
}
