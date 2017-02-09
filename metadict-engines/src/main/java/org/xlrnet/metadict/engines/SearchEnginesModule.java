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

package org.xlrnet.metadict.engines;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.xlrnet.metadict.api.engine.SearchEngineProvider;
import org.xlrnet.metadict.engines.heinzelnisse.HeinzelnisseEngineProvider;
import org.xlrnet.metadict.engines.leo.LeoEngineProvider;
import org.xlrnet.metadict.engines.nobordbok.OrdbokEngineProvider;
import org.xlrnet.metadict.engines.woxikon.WoxikonEngineProvider;

/**
 * Created by jhendess on 08.02.2017.
 */
public class SearchEnginesModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<SearchEngineProvider> binder = Multibinder.newSetBinder(binder(), SearchEngineProvider.class);
        binder.addBinding().to(HeinzelnisseEngineProvider.class);
        binder.addBinding().to(LeoEngineProvider.class);
        binder.addBinding().to(OrdbokEngineProvider.class);
        binder.addBinding().to(WoxikonEngineProvider.class);
    }
}
