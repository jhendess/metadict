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

package org.xlrnet.metadict.storage.mapdb;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.storage.StorageBackendException;
import org.xlrnet.metadict.api.storage.StorageService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Factory for creating a new {@link MapdbStorageEngine} instance based on configuration parameters.
 */
public class MapdbStorageEngineFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapdbStorageEngineFactory.class);

    private static final String PROPERTY_KEY_OPERATION_MODE = "mode";

    private static final String PROPERTY_KEY_DB_FILE = "filepath";

    private static final String PROPERTY_KEY_MMAP = "enableMmapFile";

    private static final String PROPERTY_KEY_DISABLE_TRANSACTIONS = "disableTransactions";

    private static final String PROPERTY_KEY_ASYNC_WRITE = "enableAsyncWrite";

    public StorageService fromConfiguration(Map<String, String> configuration) {
        DBMaker dbMaker = createDBMakerInstance(configuration);

        String enableMmapFileProperty = configuration.get(PROPERTY_KEY_MMAP);
        String disableTransactionProperty = configuration.get(PROPERTY_KEY_DISABLE_TRANSACTIONS);
        String enableAsyncWriteProperty = configuration.get(PROPERTY_KEY_ASYNC_WRITE);

        if ("true".equals(enableMmapFileProperty)) {
            LOGGER.info("Enabling memory mapped I/O");
            dbMaker.mmapFileEnable();
        }

        if ("true".equals(disableTransactionProperty)) {
            LOGGER.info("Disabling transactions");
            dbMaker.transactionDisable();
        }

        if ("true".equals(enableAsyncWriteProperty)) {
            LOGGER.info("Enabling asynchronous write operations");
            dbMaker.asyncWriteEnable();
        }

        return new MapdbStorageEngine(dbMaker);
    }

    private DBMaker createDBMakerInstance(Map<String, String> configuration) {
        String modeProperty = configuration.get(PROPERTY_KEY_OPERATION_MODE);
        String filepathProperty = configuration.get(PROPERTY_KEY_DB_FILE);

        checkNotNull(filepathProperty, "Database file must be specified with property" + PROPERTY_KEY_DB_FILE);

        OperationMode operationMode = OperationMode.valueOf(StringUtils.upperCase(modeProperty));

        LOGGER.info("Initializing MapDB storage backend in {} mode", operationMode);

        switch (operationMode) {
            case FILE:
                Path path = Paths.get(filepathProperty);
                LOGGER.info("Using MapDB database file '{}'", path.toAbsolutePath().toString());
                return DBMaker.newFileDB(path.toFile());
            case TEMPORARY:
                return DBMaker.newMemoryDB();
            default:
                throw new StorageBackendException("Unknown operation mode: " + operationMode);
        }
    }


    private enum OperationMode {

        TEMPORARY,

        FILE

    }
}
