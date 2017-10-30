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

package org.xlrnet.metadict.web.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.xlrnet.metadict.web.api.ResponseContainer;
import org.xlrnet.metadict.web.auth.entities.JwtPrincipal;
import org.xlrnet.metadict.web.history.entities.QueryLogEntry;
import org.xlrnet.metadict.web.history.services.QueryLoggingService;
import org.xlrnet.metadict.web.middleware.util.Constants;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource which provides access to historized query requests.
 */
@Path("/history")
@Produces(MediaType.APPLICATION_JSON)
public class HistoryResource {

    private static final String OFFSET_PARAMETER = "offset";

    private static final String SIZE_PARAMETER = "size";

    /** Service for accessing query logs. */
    private final QueryLoggingService queryLoggingService;

    @Inject
    public HistoryResource(QueryLoggingService queryLoggingService) {
        this.queryLoggingService = queryLoggingService;
    }

    @GET
    @UnitOfWork
    public Response listQueries(@Auth JwtPrincipal principal, @Min(0) @QueryParam(OFFSET_PARAMETER) int offset, @Min(0) @QueryParam(SIZE_PARAMETER) int size) {
        if (size == 0) {
            size = 10;
        }
        List<QueryLogEntry> loggedQueries = queryLoggingService.getLoggedQueries(principal, offset, size);

        List<Link> links = new ArrayList<>();

        Link nextPage = Link.fromResource(HistoryResource.class)
                .param(OFFSET_PARAMETER, Integer.toString(offset + size))
                .param(SIZE_PARAMETER, Integer.toString(size))
                .rel(Constants.NEXT_PAGE_LINK_NAME)
                .build();
        Link previousPage = Link.fromResource(HistoryResource.class)
                .param(OFFSET_PARAMETER, Integer.toString(offset - size))
                .param(SIZE_PARAMETER, Integer.toString(size))
                .rel(Constants.PREVIOUS_PAGE_LINK_NAME)
                .build();

        if (loggedQueries.size() == size) {
            links.add(nextPage);
        }

        if (offset > 0) {
            links.add(previousPage);
        }

        return Response.ok(ResponseContainer.fromSuccessful(loggedQueries, links.toArray(new Link[links.size()]))).build();
    }
}
