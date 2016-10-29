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

package org.xlrnet.metadict.web.middleware.bundles;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.servlets.assets.AssetServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * Extension of {@link AssetsBundle} that provides a servlet which overrides the <base> tag in a single-page
 * application's index.html file.
 */
public class SinglePageAppAssetsBundle extends AssetsBundle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SinglePageAppAssetsBundle.class);

    public SinglePageAppAssetsBundle() {
    }

    public SinglePageAppAssetsBundle(String path) {
        super(path);
    }

    public SinglePageAppAssetsBundle(String resourcePath, String uriPath) {
        super(resourcePath, uriPath);
    }

    public SinglePageAppAssetsBundle(String resourcePath, String uriPath, String indexFile) {
        super(resourcePath, uriPath, indexFile);
    }

    public SinglePageAppAssetsBundle(String resourcePath, String uriPath, String indexFile, String assetsName) {
        super(resourcePath, uriPath, indexFile, assetsName);
    }

    @Override
    protected AssetServlet createServlet() {
        return new SinglePageAppAssetServlet(this.getResourcePath(), this.getUriPath(), this.getIndexFile(), StandardCharsets.UTF_8);
    }
}
