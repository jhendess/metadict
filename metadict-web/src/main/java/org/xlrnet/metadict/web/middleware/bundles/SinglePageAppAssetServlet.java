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

import com.google.common.base.CharMatcher;
import com.google.common.io.Resources;
import io.dropwizard.servlets.assets.AssetServlet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Custom extension of {@link AssetServlet} that overrides the <base> tag in a single-page application's index.html
 * file.
 */
public class SinglePageAppAssetServlet extends AssetServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SinglePageAppAssetServlet.class);

    private static final long serialVersionUID = 1223013868524570404L;

    private static final CharMatcher SLASHES = CharMatcher.is('/');

    /** Name of the file that will be used for the single page index file. */
    private static final String SINGLE_PAGE_INDEX_FILE = "index.html";

    /** Path to the resources. */
    private final String resourcePath;

    /** Cached and filtered index page. */
    private byte[] filteredIndexPage;

    /** Absolute URL where the index page lies. */
    private URL indexPageUrl;

    /**
     * Creates a new {@code SinglePageAppAssetServlet} that serves static assets loaded from {@code resourceURL}
     * (typically a file: or jar: URL). The assets are served at URIs rooted at {@code uriPath}. For
     * example, given a {@code resourceURL} of {@code "file:/data/assets"} and a {@code uriPath} of
     * {@code "/js"}, an {@code AssetServlet} would serve the contents of {@code
     * /data/assets/example.js} in response to a request for {@code /js/example.js}. If a directory
     * is requested and {@code indexFile} is defined, then {@code AssetServlet} will attempt to
     * serve a file with that name in that directory. If a directory is requested and {@code
     * indexFile} is null, it will serve a 404.
     *
     * @param resourcePath
     *         the base URL from which assets are loaded
     * @param uriPath
     *         the URI path fragment in which all requests are rooted
     * @param indexFile
     *         the filename to use when directories are requested, or null to serve no indexes
     * @param defaultCharset
     *         the default character set
     */
    public SinglePageAppAssetServlet(String resourcePath, String uriPath, String indexFile, Charset defaultCharset) {
        super(resourcePath, uriPath, indexFile, defaultCharset);
        final String trimmedPath = SLASHES.trimFrom(resourcePath);
        this.resourcePath = trimmedPath.isEmpty() ? trimmedPath : trimmedPath + '/';
    }

    @Override
    public void init() throws ServletException {
        super.init();

        String contextPath = StringUtils.prependIfMissing(this.getServletConfig().getServletContext().getContextPath(), "/");
        contextPath = StringUtils.appendIfMissing(contextPath, "/");
        String indexResource = this.resourcePath + SINGLE_PAGE_INDEX_FILE;
        LOGGER.info("Preparing filtered resource {} in contextPath {}", indexResource, contextPath);

        try {
            this.indexPageUrl = getResourceUrl(indexResource);
        } catch (IllegalArgumentException e) {      // NOSONAR: No logging of exception necessary
            LOGGER.warn("No single page index found");
            return;
        }

        LOGGER.debug("Using index file {}", this.indexPageUrl);

        byte[] bytes;
        try {
            bytes = Resources.toByteArray(this.indexPageUrl);
        } catch (IOException e) {
            throw new ServletException(e);
        }

        this.filteredIndexPage = filterBaseUrlTag(bytes, contextPath);
    }

    @Override
    protected byte[] readResource(URL requestedResourceURL) throws IOException {
        if (Objects.equals(requestedResourceURL, this.indexPageUrl) && this.filteredIndexPage != null) {
            return this.filteredIndexPage;
        }
        return Resources.toByteArray(requestedResourceURL);
    }

    protected byte[] filterBaseUrlTag(byte[] byteContent, String contextPath) {
        String content = new String(byteContent);
        String replaced = StringUtils.replace(content, "{{clientBasePath}}", contextPath);
        return replaced.getBytes();
    }
}
