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

package org.xlrnet.metadict.impl.core;

import org.xlrnet.metadict.impl.util.CommonUtils;

import java.time.Duration;
import java.time.Instant;

/**
 * The class {@link SystemStatus} is a container for several system-related status information like current version and
 * uptime.
 */
public class SystemStatus {

    private static final String version = CommonUtils.getProperty("build.properties", "build.version");

    private static final String buildTime = CommonUtils.getProperty("build.properties", "build.timestamp");

    private static Instant startTime;

    private final Duration uptime;

    public SystemStatus(Duration uptime) {
        this.uptime = uptime;
    }

    /**
     * Returns the current system status.
     * @return the current system status.
     */
    protected static SystemStatus queryStatus() {
        return new SystemStatus(Duration.between(startTime, Instant.now()));
    }

    static void initialize() {
        startTime = Instant.now();
    }

    public String getBuildTime() {
        return buildTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Duration getUptime() {
        return uptime;
    }

    public String getVersion() {
        return version;
    }
}
