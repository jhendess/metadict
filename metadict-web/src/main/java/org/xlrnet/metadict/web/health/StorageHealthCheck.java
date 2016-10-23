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

import org.apache.commons.lang3.ObjectUtils;
import org.xlrnet.metadict.api.storage.StorageBackendException;
import org.xlrnet.metadict.api.storage.StorageOperationException;
import org.xlrnet.metadict.api.storage.StorageService;
import org.xlrnet.metadict.core.services.storage.DefaultStorageService;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

import javax.inject.Inject;

/**
 * Health check component for the storage backend. This tests performs basically all CRUD operations on the current
 * default storage service.
 */
public class StorageHealthCheck extends NamedHealthCheck {

    /** The target namespace in which a short sample should be written. */
    private static final String TARGET_NAMESPACE = "HEALTH_CHECK";

    private static final String TEST_KEY = "TEST";

    private static final String TEST_VALUE = "HEALTHY";

    private static final String TEST_VALUE_2 = "UPDATED_HEALTHY";

    private final StorageService storageService;

    @Inject
    public StorageHealthCheck(@DefaultStorageService StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public String getName() {
        return "storage";
    }

    @Override
    protected Result check() throws Exception {
        try {
            this.storageService.create(TARGET_NAMESPACE, TEST_KEY, TEST_VALUE);
            if (ObjectUtils.notEqual(this.storageService.read(TARGET_NAMESPACE, TEST_KEY, TEST_VALUE.getClass()), TEST_VALUE)) {
                Result.unhealthy("Read returned wrong result.");
            }
            this.storageService.update(TARGET_NAMESPACE, TEST_KEY, TEST_VALUE_2);
            this.storageService.delete(TARGET_NAMESPACE, TEST_KEY);
        } catch (StorageBackendException | StorageOperationException e) {
            Result.unhealthy("Unexpected exception", e);
        }

        return Result.healthy();
    }
}
