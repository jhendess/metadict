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

package org.xlrnet.metadict.web.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Link;
import java.util.ArrayList;
import java.util.List;

/**
 * A response container for wrapping any response objects.
 */
public class ResponseContainer<T> {

    @JsonProperty
    private ResponseStatus status;

    @JsonProperty
    private String message;

    @JsonProperty
    private T data;

    @JsonProperty
    private List<ResourceLink> links;

    public ResponseContainer() {
    }

    public ResponseContainer(ResponseStatus status, String message, T data, Link... links) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.links = buildLinks(links);
    }

    /**
     * Creates a new response container with status {@link ResponseStatus#OK} and a given data object.
     *
     * @param data
     *         The data object to return.
     * @return A constructed response container object.
     */
    public static ResponseContainer<Object> fromSuccessful(@NotNull Object data, Link... links) {
        return new ResponseContainer<>(ResponseStatus.OK, null, data, links);
    }

    /**
     * Creates a new empty response container with a given status.
     *
     * @param status
     *         The status to set in the new container.
     * @return A constructed response container object.
     */
    public static ResponseContainer<Object> withStatus(@NotNull ResponseStatus status, Link... links) {
        return new ResponseContainer<>(status, null, null, links);
    }

    @Nullable
    public T getData() {
        return this.data;
    }

    @Nullable
    public String getMessage() {
        return this.message;
    }

    @NotNull
    public ResponseStatus getStatus() {
        return this.status;
    }

    public List<ResourceLink> getLinks() {
        return links;
    }

    /**
     * Builds the internal link-map for HATEOAS patterns.
     */
    private List<ResourceLink> buildLinks(Link[] links) {
        List<ResourceLink> resourceLinks = new ArrayList<>();
        for (Link link : links) {
            resourceLinks.add(ResourceLink.fromJaxRsLink(link));
        }
        return resourceLinks;
    }
}
