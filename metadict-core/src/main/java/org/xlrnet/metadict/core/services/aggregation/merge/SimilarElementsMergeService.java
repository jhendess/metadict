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

package org.xlrnet.metadict.core.services.aggregation.merge;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.core.api.aggregation.Merges;
import org.xlrnet.metadict.core.api.aggregation.SimilarElementsMerger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

import static com.google.common.base.Preconditions.*;

/**
 * Service which provides the capability of merging similar elements in a collection. For each data type, a custom
 * {@link SimilarElementsMerger} can be defined to provide fine-granular control about how the merging should be done.
 */
@Singleton
public class SimilarElementsMergeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimilarElementsMergeService.class);

    private final Map<Class<?>, SimilarElementsMerger<?>> classMergerMap = new HashMap<>();

    private final Set<SimilarElementsMerger> defaultSimilarElementsMergers;

    SimilarElementsMergeService() {
        this.defaultSimilarElementsMergers = Collections.emptySet();
    }

    @Inject
    public SimilarElementsMergeService(Set<SimilarElementsMerger> defaultSimilarElementsMergers) {
        this.defaultSimilarElementsMergers = defaultSimilarElementsMergers;
    }

    /**
     * Initialize all injected instances of {@link SimilarElementsMerger}.
     */
    @PostConstruct
    void initialize() {
        // Do not catch possible Exceptions -> when initialization fails, the application has to stop booting
        for (SimilarElementsMerger<?> similarElementsMerger : this.defaultSimilarElementsMergers) {
            registerNewMerger(similarElementsMerger);
        }
        if (!this.classMergerMap.containsKey(Object.class)) {
            throw new IllegalStateException("Default merger for class java.lang.Object is missing");
        }
    }

    /**
     * Registers a new instance of {@link SimilarElementsMerger}. The given object will be bound for merging elements of
     * a supported class by this implementation's {@link SimilarElementsMerger#merge(Collection)}. If the type parameter
     * {@code <T>} is not set, the binding will fail. If there is already a binding for the same parameter, the binding
     * will fail, too.
     * It is recommended to not run this method manually. Registration of all available {@link SimilarElementsMerger}
     * will be automatically done via dependency injection on startup.
     *
     * @param elementsMerger
     *         The merger to add.
     * @param <T>
     *         Type of supported elements for this merger.
     * @throws IllegalArgumentException
     *         Will be thrown on configuration errors.
     */
    public synchronized <T> void registerNewMerger(@NotNull SimilarElementsMerger<T> elementsMerger) throws IllegalArgumentException {
        checkNotNull(elementsMerger);

        String mergerCanonicalName = elementsMerger.getClass().getCanonicalName();
        Merges annotation = elementsMerger.getClass().getAnnotation(Merges.class);
        checkArgument(annotation != null, "The merger %s is not annotated with @Merges", mergerCanonicalName);
        Class<?> parameterClass = annotation.value();

        SimilarElementsMerger<?> existingMerger = this.classMergerMap.get(parameterClass);
        if (existingMerger != null) {
            throw new IllegalArgumentException(String.format("Cannot add %s as merger for type %s. Duplicate binding to: %s",
                    mergerCanonicalName, parameterClass.getCanonicalName(), existingMerger.getClass().getCanonicalName()));
        }

        this.classMergerMap.put(parameterClass, elementsMerger);

        LOGGER.debug("Registered {} as merger for type {}", mergerCanonicalName, parameterClass.getCanonicalName());
    }

    @NotNull
    public <T> Collection<T> mergeElements(@NotNull Collection<T> elementsToMerge, @NotNull Class<?> typeHint) {
        validateTypeInCollection(elementsToMerge, typeHint);
        SimilarElementsMerger<T> merger = (SimilarElementsMerger<T>) findMergerForClass(typeHint);
        Collection<T> merged = merger.merge(elementsToMerge);
        return merged;
    }

    @NotNull
    private SimilarElementsMerger<?> findMergerForClass(@NotNull Class<?> clazz) {
        if (this.classMergerMap.containsKey(clazz)) {
            return this.classMergerMap.get(clazz);
        }
        checkState(clazz != Object.class, "No default merger available");
        Class<?> superclass = clazz.getSuperclass() != null ? clazz.getSuperclass() : Object.class;
        // Call this method recursively until java.lang.Object is reached
        return findMergerForClass(superclass);
    }

    private <T> void validateTypeInCollection(@NotNull Collection<T> elementsToMerge, @NotNull Class<?> typeHint) {
        for (T t : elementsToMerge) {
            checkArgument(typeHint.isAssignableFrom(t.getClass()), "Object of class %s cannot be assigned to %s", t.getClass(), typeHint.getCanonicalName());
            checkNotNull(t, "Object in mergeable collection may not be null");
        }
    }
}
