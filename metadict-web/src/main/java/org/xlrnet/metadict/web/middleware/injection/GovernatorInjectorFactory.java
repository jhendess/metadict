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

package org.xlrnet.metadict.web.middleware.injection;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import com.netflix.governator.guice.LifecycleInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.web.middleware.util.CustomInjectorFactory;
import ru.vyarus.dropwizard.guice.injector.InjectorFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Guice injector factory to start Netflix Governator. Provides support for overriding modules. See
 * https://xvik.github.io/dropwizard-guicey/4.0.1/guide/test/#overriding-beans.
 */
public class GovernatorInjectorFactory implements InjectorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomInjectorFactory.class);

    /**
     * Custom modules which override the existing ones.
     */
    private static Set<Module> customModules;

    @Override
    public Injector createInjector(Stage stage, Iterable<? extends Module> modules) {
        Iterable<? extends Module> finalModules = modules;
        if (customModules != null) {
            LOGGER.warn("------------------------------------------------------------------");
            LOGGER.warn("Creating injector with overridden modules - use only in TEST code!");
            LOGGER.warn("------------------------------------------------------------------");
            finalModules = Lists.newArrayList(Modules.override(modules).with(customModules));
        }
        return LifecycleInjector.builder().withModules(finalModules).inStage(stage).build().createInjector();
    }

    public static void override(Module... modules) {
        customModules = new HashSet<>();
        customModules.addAll(Arrays.asList(modules));
    }

    public static void clear() {
        customModules.clear();
    }
}
