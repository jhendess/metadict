/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Jakob Hendeß
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

package org.xlrnet.metadict.core;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.xlrnet.metadict.api.storage.StorageService;
import org.xlrnet.metadict.api.storage.StorageServiceProvider;
import org.xlrnet.metadict.core.api.aggregation.MergeStrategy;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;
import org.xlrnet.metadict.core.api.query.QueryPlanExecutionStrategy;
import org.xlrnet.metadict.core.api.query.QueryPlanningStrategy;
import org.xlrnet.metadict.core.services.aggregation.merge.BilingualEntryMerger;
import org.xlrnet.metadict.core.services.aggregation.merge.DefaultSimilarElementsMerger;
import org.xlrnet.metadict.core.services.aggregation.merge.DummyMergeStrategy;
import org.xlrnet.metadict.core.services.query.CachedLinearExecutionStrategy;
import org.xlrnet.metadict.core.services.query.SimpleQueryPlanningStrategy;
import org.xlrnet.metadict.core.services.storage.DefaultStorageService;
import org.xlrnet.metadict.core.services.storage.InMemoryStorageProvider;
import org.xlrnet.metadict.core.services.storage.StorageServiceFactory;
import org.xlrnet.metadict.storage.mapdb.MapdbStorageProvider;

/**
 * Guice module which configures the metadict core.
 */
public class CoreModule extends AbstractModule {

    @Override
    protected void configure() {
        // Configure storage system
        Multibinder<StorageServiceProvider> storageBinder = Multibinder.newSetBinder(binder(), StorageServiceProvider.class);
        storageBinder.addBinding().to(InMemoryStorageProvider.class);
        storageBinder.addBinding().to(MapdbStorageProvider.class);
        bind(StorageService.class).annotatedWith(DefaultStorageService.class).toProvider(StorageServiceFactory.class);

        // Configure strategies
        bind(QueryPlanExecutionStrategy.class).to(CachedLinearExecutionStrategy.class);
        bind(QueryPlanningStrategy.class).to(SimpleQueryPlanningStrategy.class);
        bind(MergeStrategy.class).to(DummyMergeStrategy.class);

        // Configure mergers
        Multibinder<SimilarElementsMerger> similarElementsMergerBinder = Multibinder.newSetBinder(binder(), SimilarElementsMerger.class);
        similarElementsMergerBinder.addBinding().to(DefaultSimilarElementsMerger.class);
        similarElementsMergerBinder.addBinding().to(BilingualEntryMerger.class);
    }
}
