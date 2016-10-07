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

package org.xlrnet.metadict.web.resources;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jetbrains.annotations.NotNull;
import org.xlrnet.metadict.web.api.MethodDescription;
import org.xlrnet.metadict.web.api.ResourceDescription;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Endpoint which returns an overview of all currently supported REST endpoints (Resteasy-specifc implementation).
 */
@Path("/")
public class OverviewResource {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ResourceDescription> getAvailableEndpoints(@Context Dispatcher dispatcher) {
        return ResourceDescription.fromBoundResourceInvokers(getResoureDescriptions((ResourceMethodRegistry) dispatcher.getRegistry()));
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public Response getAvailableEndpointsHtml(@Context Dispatcher dispatcher) {
        StringBuilder sb = new StringBuilder();
        List<ResourceDescription> descriptions = getAvailableEndpoints(dispatcher);

        sb.append("<h1>").append("REST interface overview").append("</h1>");

        for (ResourceDescription resource : descriptions) {
            sb.append("<h2>").append(resource.getBasePath()).append("</h2>");
            sb.append("<ul>");

            for (MethodDescription method : resource.getCalls()) {
                sb.append("<li> ").append(method.getMethod()).append(" ");
                sb.append("<strong>").append(method.getFullPath()).append("</strong>");

                sb.append("<ul>");

                if (method.getConsumes() != null) {
                    sb.append("<li>").append("Consumes: ").append(Arrays.asList(method.getConsumes())).append("</li>");
                }

                if (method.getProduces() != null) {
                    sb.append("<li>").append("Produces: ").append(Arrays.asList(method.getProduces())).append("</li>");
                }

                sb.append("</ul>");
            }

            sb.append("</ul>");
        }

        return Response.ok(sb.toString()).build();

    }

    @NotNull
    private Set<Map.Entry<String, List<ResourceInvoker>>> getResoureDescriptions(ResourceMethodRegistry registry) {
        return registry.getBounded().entrySet();
    }
}
