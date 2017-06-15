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

package org.xlrnet.metadict.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Link;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Link to a relative resource. This is used in the HATEOAS pattern to navigate between resources.
 */
public class ResourceLink {

    /** The relationship to the link. */
    @JsonProperty
    private final String rel;

    /** The link to the resource, relative to the API base URL. */
    @JsonProperty
    private final String href;

    public ResourceLink(String rel, String href) {
        this.rel = rel;
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public String getHref() {
        return href;
    }

    public static ResourceLink fromJaxRsLink(Link link) {
        StringBuilder hrefBuilder = new StringBuilder(link.getUri().toString());
        Map<String, String> params = link.getParams();

        String parameters = params.entrySet().stream()
                .filter(e -> !("title".equals(e.getKey()) || "rel".equals(e.getKey()) || "type".equals(e.getKey())))
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        if (StringUtils.isNotEmpty(parameters)) {
            hrefBuilder.append("?").append(parameters);
        }

        return new ResourceLink(link.getRel(), hrefBuilder.toString());
    }
}
