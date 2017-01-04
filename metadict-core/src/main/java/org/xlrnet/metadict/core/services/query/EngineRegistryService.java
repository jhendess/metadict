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

package org.xlrnet.metadict.core.services.query;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.engine.*;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.core.services.autotest.AutoTestService;
import org.xlrnet.metadict.core.util.BilingualDictionaryUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Class for loading and managing all available {@link SearchEngineProvider}.
 * <p>
 * Since this object is {@link Singleton}, only one instance will be running at the
 * same time.
 */
@Singleton
public class EngineRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(EngineRegistryService.class);

    Multimap<BilingualDictionary, String> dictionaryEngineNameMap = ArrayListMultimap.create();

    Multimap<Language, String> languageEngineNameMap = ArrayListMultimap.create();

    private Map<String, EngineDescription> engineDescriptionMap = new HashMap<>();

    private Map<String, FeatureSet> featureSetMap = new HashMap<>();

    private Map<String, SearchEngine> searchEngineMap = new HashMap<>();

    private List<BilingualDictionary> supportedDictionaryList;

    /** List of available search providers. */
    private Set<SearchEngineProvider> searchProviderInstances;

    /** The auto test manager. */
    private AutoTestService autoTestService;

    public EngineRegistryService() {
    }

    @Inject
    public EngineRegistryService(Set<SearchEngineProvider> searchProviderInstances, AutoTestService autoTestService) {
        this.searchProviderInstances = searchProviderInstances;
        this.autoTestService = autoTestService;
    }

    /**
     * Returns the amount of currently registered search engines. Search engines are provided by implementations of
     * {@link SearchEngineProvider} and can be registered using {@link #registerSearchProvider(SearchEngineProvider)}.
     *
     * @return the amount of currently registered search engines.
     */
    public int countRegisteredEngines() {
        return this.searchEngineMap.size();
    }

    /**
     * Returns the concrete {@link SearchEngine} that is registered under the given name. This name should be the
     * canonical class name of the engine. The registered engines can be queried by using {@link
     * #getRegisteredEngineNames()}.
     *
     * @param engineName
     *         Name of the registered engine
     * @return a concrete {@link SearchEngine}.
     * @throws UnknownSearchEngineException
     *         Will be thrown, if no engine is registered under the given name.
     */
    @NotNull
    public SearchEngine getEngineByName(String engineName) throws UnknownSearchEngineException {
        if (!this.searchEngineMap.containsKey(engineName)) {
            throw new UnknownSearchEngineException(engineName);
        }
        return this.searchEngineMap.get(engineName);
    }

    /**
     * Returns the concrete {@link EngineDescription} for the {@link SearchEngine} that is registered under the given
     * name. This name should be the canonical class name of the engine. The registered engines can be queried by using
     * {@link #getRegisteredEngineNames()}.
     *
     * @param engineName
     *         Name of the registered engine
     * @return a concrete {@link EngineDescription}.
     * @throws UnknownSearchEngineException
     *         Will be thrown, if no engine is registered under the given name.
     */
    @NotNull
    public EngineDescription getEngineDescriptionByName(String engineName) {
        if (!this.engineDescriptionMap.containsKey(engineName)) {
            throw new UnknownSearchEngineException(engineName);
        }
        return this.engineDescriptionMap.get(engineName);
    }

    /**
     * Returns the concrete {@link FeatureSet} for the {@link SearchEngine} that is registered under the given
     * name. This name should be the canonical class name of the engine. The registered engines can be queried by using
     * {@link #getRegisteredEngineNames()}.
     *
     * @param engineName
     *         Name of the registered engine
     * @return a concrete {@link EngineDescription}.
     * @throws UnknownSearchEngineException
     *         Will be thrown, if no engine is registered under the given name.
     */
    @NotNull
    public FeatureSet getFeatureSetByName(String engineName) {
        if (!this.featureSetMap.containsKey(engineName)) {
            throw new UnknownSearchEngineException(engineName);
        }
        return this.featureSetMap.get(engineName);
    }

    /**
     * Returns an unmodifiable set of the currently registered search engine names. The registered names should be the
     * canonical class name of the {@link SearchEngine} implementation.
     *
     * @return an unmodifiable set of the currently registered search engine names.
     */
    @NotNull
    public Set<String> getRegisteredEngineNames() {
        return Collections.unmodifiableSet(this.searchEngineMap.keySet());
    }

    /**
     * Returns the names of all engines that support the given {@link BilingualDictionary} for bilingual look-ups.
     *
     * @param dictionary
     *         The dictionary to look for.
     * @return a collection of engines that support the given dictionary.
     */
    @NotNull
    public Collection<String> getSearchEngineNamesByDictionary(@NotNull BilingualDictionary dictionary) {
        return Collections.unmodifiableCollection(this.dictionaryEngineNameMap.get(dictionary));
    }

    /**
     * Returns the names of all engines that support the given {@link Language} for monolingual look-ups.
     *
     * @param language
     *         The language to look for.
     * @return a collection of engines that support the given language.
     */
    @NotNull
    public Collection<String> getSearchEngineNamesByLanguage(@NotNull Language language) {
        return Collections.unmodifiableCollection(this.languageEngineNameMap.get(language));
    }

    /**
     * Returns a collection of all currently registered dictionaries that are supported by at least one search engine.
     * If a engine registered a bothWay-dictionary, this method will also return one-way configurations of this
     * dictionary.
     * <p>
     * Example:
     * If an engine registered itself as <code>de-en__bothWay</code>, this method will return
     * <code>de-en__bothWay</code>, <code>de-en</code> and <code>en-de</code>
     *
     * @return a collection of all currently registered dictionaries that are supported by at least one search engine.
     */
    @NotNull
    public Collection<BilingualDictionary> getSupportedDictionaries() {
        return Collections.unmodifiableCollection(this.supportedDictionaryList);
    }

    /**
     * Returns the internal {@link AutoTestService} instance.
     *
     * @return the internal {@link AutoTestService} instance.
     */
    @NotNull
    public AutoTestService getAutoTestService() {
        return this.autoTestService;
    }

    @PostConstruct
    public void initialize() {
        logger.info("Registering search providers...");
        int failedProviders = 0;

        for (SearchEngineProvider searchEngineProvider : this.searchProviderInstances) {

            try {
                registerSearchProvider(searchEngineProvider);
            } catch (Exception e) {
                logger.error("Registering search provider {} failed", searchEngineProvider.getClass().getCanonicalName(), e);
                failedProviders++;
            }
        }

        if (failedProviders > 0) {
            logger.warn("Registration failed on {} {}", failedProviders, failedProviders > 1 ? "providers" : "provider");
        }

        this.supportedDictionaryList = Lists.newArrayList(this.dictionaryEngineNameMap.keySet());
        BilingualDictionaryUtils.sortDictionaryListAlphabetically(this.supportedDictionaryList);
    }

    /**
     * Register the given {@link SearchEngineProvider} in the internal registry. The registration may fail, if any of
     * the mandatory methods of the {@link SearchEngineProvider} return null or a provider with the same class name is
     * already registered.
     *
     * @param searchEngineProvider
     *         The {@link SearchEngineProvider} that should be registered.
     */
    void registerSearchProvider(@NotNull SearchEngineProvider searchEngineProvider) {
        String canonicalProviderName = searchEngineProvider.getClass().getCanonicalName();

        checkState(!this.searchEngineMap.containsKey(canonicalProviderName), "Provider %s is already registered", canonicalProviderName);

        EngineDescription engineDescription = searchEngineProvider.getEngineDescription();
        FeatureSet featureSet = searchEngineProvider.getFeatureSet();
        validateSearchProvider(canonicalProviderName, engineDescription, featureSet);

        SearchEngine searchEngine = searchEngineProvider.newEngineInstance();
        checkNotNull(searchEngine, "Search provider returned null engine", canonicalProviderName);

        registerAutoTestSuite(searchEngineProvider, searchEngine);

        String canonicalEngineName = searchEngine.getClass().getCanonicalName();

        this.engineDescriptionMap.put(canonicalEngineName, engineDescription);
        this.featureSetMap.put(canonicalEngineName, featureSet);
        this.searchEngineMap.put(canonicalEngineName, searchEngine);
        registerDictionariesFromFeatureSet(canonicalEngineName, featureSet);

        logger.info("Registered engine {} from provider {}", canonicalEngineName, canonicalProviderName);
    }

    private void registerAutoTestSuite(@NotNull SearchEngineProvider searchEngineProvider, @NotNull SearchEngine searchEngine) {
        try {
            AutoTestSuite testSuite = searchEngineProvider.getAutoTestSuite();
            if (testSuite == null) {
                logger.warn("Provider {} provides no auto test suite", searchEngineProvider.getClass().getCanonicalName());
            } else {
                this.autoTestService.registerAutoTestSuite(searchEngine, testSuite);
            }
        } catch (Exception e) {
            logger.error("Initializing auto test suite from provider {} failed", searchEngineProvider.getClass().getCanonicalName(), e);
        }
    }

    private void registerDictionariesFromFeatureSet(@NotNull String canonicalEngineName, @NotNull FeatureSet featureSet) {
        for (BilingualDictionary dictionary : featureSet.getSupportedBilingualDictionaries()) {
            registerDictionary(canonicalEngineName, dictionary);
        }
        for (Language language : featureSet.getSupportedLexicographicLanguages()) {
            this.languageEngineNameMap.put(language, canonicalEngineName);
        }
    }

    private void registerDictionary(@NotNull String canonicalEngineName, @NotNull BilingualDictionary dictionary) {
        if (!this.dictionaryEngineNameMap.containsEntry(dictionary, canonicalEngineName)) {
            this.dictionaryEngineNameMap.put(dictionary, canonicalEngineName);
        }
        if (dictionary.isBidirectional()) {
            // Register inverted bidirectional dictionary
            BilingualDictionary inverseBidirectional = BilingualDictionary.inverse(dictionary);
            if (!this.dictionaryEngineNameMap.containsEntry(inverseBidirectional, canonicalEngineName)) {
                this.dictionaryEngineNameMap.put(inverseBidirectional, canonicalEngineName);
            }
            // Register as non-bidirectional to improve lookup speeds
            BilingualDictionary simpleDictionary = BilingualDictionary.fromLanguages(dictionary.getSource(), dictionary.getTarget(), false);
            if (!this.dictionaryEngineNameMap.containsEntry(simpleDictionary, canonicalEngineName)) {
                this.dictionaryEngineNameMap.put(simpleDictionary, canonicalEngineName);
            }
            BilingualDictionary inverseSimpleDictionary = BilingualDictionary.inverse(simpleDictionary);
            if (!this.dictionaryEngineNameMap.containsEntry(inverseSimpleDictionary, canonicalEngineName)) {
                this.dictionaryEngineNameMap.put(inverseSimpleDictionary, canonicalEngineName);
            }
        }
    }

    private void validateSearchProvider(@NotNull String canonicalName, @Nullable EngineDescription engineDescription, @Nullable FeatureSet featureSet) {
        checkNotNull(engineDescription, "Search provider %s returned null description", canonicalName);
        checkNotNull(featureSet, "Search provider %s returned null feature set", canonicalName);

        checkNotNull(featureSet.getSupportedBilingualDictionaries(), "Feature set of search provider %s has null on supported dictionaries", canonicalName);

        for (BilingualDictionary dictionary : featureSet.getSupportedBilingualDictionaries()) {
            checkNotNull(dictionary, "Dictionary from search provider %s may not be null", canonicalName);
            checkNotNull(dictionary.getSource(), "Input language in dictionary from search provider %s may not be null", canonicalName);
            checkNotNull(dictionary.getTarget(), "Output language in dictionary from search provider %s may not be null", canonicalName);
        }
    }

}
