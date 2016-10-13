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

package org.xlrnet.metadict.web.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.xlrnet.metadict.core.query.QueryResponse;
import org.xlrnet.metadict.web.jackson.mixins.QueryResponseMixIn;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Extended Resteasy JSON producer using jackson. Allows the serialization of java.time.* objects and provides
 * additional configuration.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonResteasyProducer implements ContextResolver<ObjectMapper> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            // Find additional modules
            .findAndRegisterModules()
            // Register custom module for Metadict serializers
            .registerModule(new MetadictModule())
            // Don't print null-values
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            // Don't write timestamps
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            // Don't fail on unknown values
            //.configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            // Indentation for output
            //.configure(SerializationFeature.INDENT_OUTPUT, true)
            // Allow field names without quotes
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            // Enable a custom mixin which prevents transmitting ungrouped bilingual entries
            .addMixIn(QueryResponse.class, QueryResponseMixIn.class);

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public ObjectMapper getContext(Class<?> objectType) {
        return objectMapper;
    }

}
