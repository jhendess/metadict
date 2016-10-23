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

package org.xlrnet.metadict.web.middleware.jersey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.web.api.ResponseContainer;
import org.xlrnet.metadict.web.api.ResponseStatus;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Generic exception mapper which catches all
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExceptionMapper.class);

    @Override
    public Response toResponse(RuntimeException exception) {
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        LOGGER.error("An unexpected internal error occured", exception);
        return Response
                .serverError()
                .entity(new ResponseContainer<Void>(ResponseStatus.ERROR, "An unexpected internal error occured.", null))
                .build();
    }
}