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

package org.xlrnet.metadict.api.storage;

import com.google.common.base.MoreObjects;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.event.ListenerConfiguration;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for creating new {@link StorageDescription} objects.
 */
public class StorageDescriptionBuilder {

    private String authorName;

    private String authorLink;

    private String copyright;

    private String storageEngineName;

    private String storageEngineLink;

    private String license;

    private String backendCopyright;

    private String backendLink;

    private String backendName;

    private List<ListenerConfiguration<StorageEventType, StorageEventListener>> listeners = new ArrayList<>();

    StorageDescriptionBuilder() {

    }

    /**
     * Add a new Listener to the storage description.
     *
     * @param listenerConfiguration
     *         Listener configuration which listens to {@link StorageEventType} and comes with an attached {@link
     *         StorageEventListener}.
     * @return this builder.
     */
    public StorageDescriptionBuilder addListenerConfiguration(ListenerConfiguration<StorageEventType, StorageEventListener> listenerConfiguration) {
        checkNotNull(listenerConfiguration);
        listeners.add(listenerConfiguration);
        return this;
    }

    /**
     * Create a new instance of {@link StorageDescription}.
     *
     * @return this builder.
     */
    @NotNull
    public StorageDescription build() {
        return new ImmutableStorageDescription(authorName, authorLink, copyright, storageEngineName, storageEngineLink, license, backendCopyright, backendLink, backendName, listeners);
    }

    /**
     * Set the url to the website of the author.
     *
     * @return this builder.
     */
    @NotNull
    public StorageDescriptionBuilder setAuthorLink(@NotNull String authorLink) {
        this.authorLink = authorLink;
        return this;
    }

    /**
     * Set the name of the author who developed the engine.
     *
     * @return the name of the author who developed the engine.
     */
    public StorageDescriptionBuilder setAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    /**
     * Set copyright information about the underlying search backend.
     *
     * @return this builder.
     */
    public StorageDescriptionBuilder setBackendCopyright(String backendCopyright) {
        this.backendCopyright = backendCopyright;
        return this;
    }

    /**
     * Set a link to the backend. This is usually the url of the underlying storage engine that is being called.
     *
     * @return this builder.
     */
    public StorageDescriptionBuilder setBackendLink(String backendLink) {
        this.backendLink = backendLink;
        return this;
    }

    /**
     * Returns the name of the backend. This is usually the name of the underlying storage engine that is being called.
     *
     * @return this builder.
     */
    public StorageDescriptionBuilder setBackendName(String backendName) {
        this.backendName = backendName;
        return this;
    }

    /**
     * Set copyright information about the engine.
     *
     * @return this builder.
     */
    @NotNull
    public StorageDescriptionBuilder setCopyright(@NotNull String copyright) {
        this.copyright = copyright;
        return this;
    }

    /**
     * Set license information of the engine.
     *
     * @return this builder.
     */
    @NotNull
    public StorageDescriptionBuilder setLicense(@NotNull String license) {
        this.license = license;
        return this;
    }

    /**
     * Set a link to the storage's engines website. This link should contain either general information about the
     * engine or the full source code.
     *
     * @return this builder.
     */
    @NotNull
    public StorageDescriptionBuilder setStorageEngineLink(@NotNull String storageEngineLink) {
        this.storageEngineLink = storageEngineLink;
        return this;
    }

    /**
     * Set the name of the engine. This parameter has to be set always or the core might not load the engine.
     *
     * @return this builder.
     */
    @NotNull
    public StorageDescriptionBuilder setStorageEngineName(@NotNull String storageEngineName) {
        this.storageEngineName = storageEngineName;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("authorLink", authorLink)
                .add("copyright", copyright)
                .add("storageEngineName", storageEngineName)
                .add("storageEngineLink", storageEngineLink)
                .add("license", license)
                .add("backendCopyright", backendCopyright)
                .add("backendLink", backendLink)
                .add("backendName", backendName)
                .add("listeners", listeners)
                .toString();
    }
}
