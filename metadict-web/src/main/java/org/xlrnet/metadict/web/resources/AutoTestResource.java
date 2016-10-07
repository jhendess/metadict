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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.core.main.EngineRegistryService;
import org.xlrnet.metadict.web.api.ResponseContainer;
import org.xlrnet.metadict.web.api.ResponseStatus;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST service for invoking the internal auto testing of search engines.
 * <p>
 * Currently the following modes are supported: <ul> <li>Run all tests: /api/autotest/all invokes all internally
 * registered test suites and returns the test report from the core component.</li> </ul>
 */
@Path("/autotest")
public class AutoTestResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoTestResource.class);

    /** The central engine registry. */
    private EngineRegistryService engineRegistryService;

    public AutoTestResource() {
    }

    @Inject
    public AutoTestResource(EngineRegistryService engineRegistryService) {
        this.engineRegistryService = engineRegistryService;
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response invokeAllAutoTests() {
        try {
            return Response.ok(ResponseContainer.fromSuccessful(this.engineRegistryService.getAutoTestManager().runAllRegisteredAutoTests())).build();
        } catch (Exception e) {
            LOGGER.error("Executing all auto tests failed", e);
            return Response.ok(new ResponseContainer<>(ResponseStatus.INTERNAL_ERROR, e.getMessage(), null)).build();
        }
    }

}
