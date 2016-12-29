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

package org.xlrnet.metadict.core.services.status;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.core.util.CommonUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;

/**
 * Services for providing information about the current system state (versions, etc.).
 */
@Singleton
public class SystemStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemStatusService.class);

    /**
     * Constant with unknown value.
     */
    private static final String UNKNOWN = "UNKNOWN";

    /**
     * The start time of the application.
     */
    private final static Instant START_TIME = Instant.now();

    /**
     * Build time of the application.
     */
    private static final String BUILD_TIME = CommonUtils.getProperty("build.properties", "build.timestamp", UNKNOWN);

    /**
     * The version of this application.
     */
    private static final String VERSION = CommonUtils.getProperty("build.properties", "build.version", UNKNOWN);

    /**
     * The scm revision of this application.
     */
    private static final String REVISION = CommonUtils.getProperty("build.properties", "build.revision", UNKNOWN);

    /**
     * Returns the current system status.
     *
     * @return the current system status.
     */
    public SystemStatus queryStatus() {
        return new SystemStatus(Duration.between(START_TIME, Instant.now()));
    }

    @PostConstruct
    private void initialize() {
        LOGGER.info("Version is {} in revision {} built at {}.", VERSION, REVISION, BUILD_TIME);
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutting down Metadict...");
    }

    /**
     * Container class with information about the current system status.
     */
    public static class SystemStatus {

        /**
         * The uptime of the application.
         */
        private final Duration uptime;

        SystemStatus(Duration uptime) {
            this.uptime = uptime;
        }

        public String getVersion() {
            return VERSION;
        }

        public String getRevision() {
            return REVISION;
        }

        public String getBuildTime() {
            return BUILD_TIME;
        }

        public Instant getStartTime() {
            return START_TIME;
        }

        public Duration getUptime() {
            return this.uptime;
        }
    }
}
