package org.xlrnet.metadict.web.middleware.util;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.injector.InjectorFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom injector factory which provides support for overriding modules. See https://xvik.github.io/dropwizard-guicey/4.0.1/guide/test/#overriding-beans
 */
public class CustomInjectorFactory implements InjectorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomInjectorFactory.class);

    /**
     * Modules which should be injected by default.
     */
    private static ThreadLocal<Modules[]> modules = new ThreadLocal<>();

    /**
     * Custom modules which override the existing ones.
     */
    private static Set<Module> customModules;

    @Override
    public Injector createInjector(Stage stage, Iterable<? extends Module> modules) {
        if (customModules == null) {
            LOGGER.debug("Creating default injector");
            return Guice.createInjector(stage, modules);
        } else {
            LOGGER.warn("------------------------------------------------------------------");
            LOGGER.warn("Creating injector with overridden modules - use only in TEST code!");
            LOGGER.warn("------------------------------------------------------------------");
            return Guice.createInjector(stage, Lists.newArrayList(Modules.override(modules).with(customModules)));
        }
    }

    public static void override(Module... modules) {
        customModules = new HashSet<>();
        customModules.addAll(Arrays.asList(modules));
    }

    public static void clear() {
        customModules.clear();
    }
}
