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

package org.xlrnet.metadict.web.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.xlrnet.metadict.core.api.config.MetadictConfiguration;
import org.xlrnet.metadict.core.api.config.StorageConfiguration;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Configuration class for standalone deployment using dropwizard.
 */
public class MappedJsonConfiguration extends Configuration implements MetadictConfiguration {

    @NotNull
    @JsonProperty("storage")
    private StorageConfigurationImpl storage;

    @Override
    public StorageConfigurationImpl getStorageConfiguration() {
        return this.storage;
    }


    private static class StorageConfigurationImpl implements StorageConfiguration {

        @JsonProperty("engines")
        private Map<String, Map<String, String>> engines;

        @NotNull
        @JsonProperty("default")
        private String defaultStorage;

        @Override
        public String getDefaultStorage() {
            return this.defaultStorage;
        }

        @Override
        @JsonProperty("engines")
        public Map<String, Map<String, String>> getEngineConfigurations() {
            return this.engines;
        }

    }
}
