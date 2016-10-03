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
public class ImmutableStorageDescription implements StorageDescription {

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

    ImmutableStorageDescription(String authorName, String authorLink, String copyright, String storageEngineName, String storageEngineLink, String license, String backendCopyright, String backendLink, String backendName, List<ListenerConfiguration<StorageEventType, StorageEventListener>> listeners) {
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

    public static StorageDescriptionBuilder builder() {
        return new StorageDescriptionBuilder();
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public String getAuthorLink() {
        return authorLink;
    }

    @Override
    public String getCopyright() {
        return copyright;
    }

    @Override
    public String getEngineName() {
        return storageEngineName;
    }

    @Override
    public String getEngineLink() {
        return storageEngineLink;
    }

    @Override
    public String getLicense() {
        return license;
    }

    @NotNull
    @Override
    public List<ListenerConfiguration<StorageEventType, StorageEventListener>> getListeners() {
        return listeners;
    }

    @Override
    public String getBackendCopyright() {
        return backendCopyright;
    }

    @Override
    public String getBackendLink() {
        return backendLink;
    }

    @Override
    public String getBackendName() {
        return backendName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableStorageDescription)) return false;
        ImmutableStorageDescription that = (ImmutableStorageDescription) o;
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
