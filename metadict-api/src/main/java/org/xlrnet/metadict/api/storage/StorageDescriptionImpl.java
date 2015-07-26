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
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.api.event.ListenerConfiguration;

import java.util.List;

/**
 * Immutable implementation for {@link StorageDescription}.
 */
public class StorageDescriptionImpl implements StorageDescription {

    private static final long serialVersionUID = 2358208204021337882L;

    private final String authorName;

    private final String authorLink;

    private final String copyright;

    private final String storageEngineName;

    private final String storageEngineLink;

    private final String license;

    private final String backendCopyright;

    private final String backendLink;

    private final String backendName;

    private final List<ListenerConfiguration<StorageEventType, StorageEventListener>> listeners;

    public StorageDescriptionImpl(String authorName, String authorLink, String copyright, String storageEngineName, String storageEngineLink, String license, String backendCopyright, String backendLink, String backendName, List<ListenerConfiguration<StorageEventType, StorageEventListener>> listeners) {
        this.authorName = authorName;
        this.authorLink = authorLink;
        this.copyright = copyright;
        this.storageEngineName = storageEngineName;
        this.storageEngineLink = storageEngineLink;
        this.license = license;
        this.backendCopyright = backendCopyright;
        this.backendLink = backendLink;
        this.backendName = backendName;
        this.listeners = listeners;
    }

    /**
     * Returns the name of the author who developed the engine.
     *
     * @return the name of the author who developed the engine.
     */
    @Override
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Returns the url to the website of the author.
     *
     * @return the url to the website of the author.
     */
    @Override
    public String getAuthorLink() {
        return authorLink;
    }

    /**
     * Returns copyright information about the engine.
     *
     * @return copyright information about the engine.
     */
    @Override
    public String getCopyright() {
        return copyright;
    }

    /**
     * Returns the name of the engine. This parameter has to be set always or the core might not load the engine.
     *
     * @return the name of the engine.
     */
    @Override
    public String getEngineName() {
        return storageEngineName;
    }

    /**
     * Returns a link to the storage's website. This link should contain either general information about the engine or
     * the full source code.
     *
     * @return a link to the storage's website.
     */
    @Override
    public String getEngineLink() {
        return storageEngineLink;
    }

    /**
     * Returns license information about the storage.
     *
     * @return license information about the storage.
     */
    @Override
    public String getLicense() {
        return license;
    }

    /**
     * Return a list of attached listeners. The order of which listeners will be called depends only on the order of
     * elements in the list.
     *
     * @return a list of attached listeners.
     */
    @NotNull
    @Override
    public List<ListenerConfiguration<StorageEventType, StorageEventListener>> getListeners() {
        return listeners;
    }

    /**
     * Returns copyright information about the underlying search backend.
     *
     * @return copyright information about the underlying search backend.
     */
    @Override
    public String getBackendCopyright() {
        return backendCopyright;
    }

    /**
     * Returns a link to the backend. This is usually the url of the underlying search engine that is being called.
     *
     * @return a link to the backend.
     */
    @Override
    public String getBackendLink() {
        return backendLink;
    }

    /**
     * Returns the name of the backend. This is usually the name of the underlying search engine that is being called.
     *
     * @return the name of the backend.
     */
    @Override
    public String getBackendName() {
        return backendName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StorageDescriptionImpl)) return false;
        StorageDescriptionImpl that = (StorageDescriptionImpl) o;
        return Objects.equal(authorLink, that.authorLink) &&
                Objects.equal(copyright, that.copyright) &&
                Objects.equal(storageEngineName, that.storageEngineName) &&
                Objects.equal(storageEngineLink, that.storageEngineLink) &&
                Objects.equal(license, that.license) &&
                Objects.equal(backendCopyright, that.backendCopyright) &&
                Objects.equal(backendLink, that.backendLink) &&
                Objects.equal(backendName, that.backendName) &&
                Objects.equal(listeners, that.listeners);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authorLink, copyright, storageEngineName, storageEngineLink, license, backendCopyright, backendLink, backendName, listeners);
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
