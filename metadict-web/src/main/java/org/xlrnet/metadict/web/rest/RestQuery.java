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

package org.xlrnet.metadict.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.language.Dictionary;
import org.xlrnet.metadict.impl.core.MetadictCore;
import org.xlrnet.metadict.impl.query.QueryResponse;
import org.xlrnet.metadict.web.api.ResponseContainer;
import org.xlrnet.metadict.web.api.ResponseStatus;
import org.xlrnet.metadict.web.util.DictionaryUtils;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * REST service for sending a query to the Metadict core.
 * <p>
 * This endpoint can be accessed in two different ways:
 * <ul>
 * <li>Two-way dictionary query: call /api/query/DICTIONARIES/{REQUEST} where DICTIONARIES is the list of
 * dictionary languages that should be queried and REQUEST is the concrete query string that should be sent to
 * metadict.
 * This method invokes an automatic two-way query on the core and tries to resolve the internal dictionaries with this
 * preference. This is usually the preferred way for querying.</li>
 * <li>One-way dictionary query: call /api/uniquery/DICTIONARIES/{REQUEST} where DICTIONARIES is the list of
 * dictionary languages that should be queried and REQUEST is the concrete query string that should be sent to
 * metadict.
 * This method invokes the query exactly with the provided dictionaries. Use this method if you want to search only in
 * one direction.</li>
 * </ul>
 */
@Path("/")
public class RestQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestQuery.class);

    @Inject
    MetadictCore metadictCore;

    /**
     * Issue a two-way dictionary query.
     * <p>
     * Endpoint: /api/query/DICTIONARIES/{REQUEST} where DICTIONARIES is the list of dictionary languages that should
     * be queried and REQUEST is the concrete query string that should be sent to metadict. See the concrete parameter
     * description for more information about the parameters.
     * This method invokes an automatic two-way query on the core and tries to resolve the internal dictionaries with
     * this preference. This is usually the preferred way for querying.
     *
     * @param dictionaryString
     *         A comma-separated list of dictionaries to call. Each dictionary's language is separated with a minus
     *         ("-"). If you need to query a concrete dialect, use an underscore ("_") after the language identifiers.
     *         <p>
     *         Example: "de-en,de-no_ny" will issue a query between german and english (i.e. the two identifiers "de"
     *         and "en") and also german and norwegian nynorsk (i.e. the identifier "de" and the dialect "ny" of
     *         language "no").
     * @param queryRequest
     *         The concrete query string that should be passed to the internal engines. Special URI unescaping is
     *         handled automatically through the underlying JAX-RS engine.
     */
    @GET
    @Path("/query/{dictionaries}/{request}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response bidirectionalQuery(@PathParam("dictionaries") String dictionaryString, @PathParam("request") String queryRequest) {
        return internalExecuteQuery(dictionaryString, queryRequest, true);
    }

    /**
     * Issue a one-way dictionary query.
     * <p>
     * Endpoint: /api/uniquery/DICTIONARIES/{REQUEST} where DICTIONARIES is the list of dictionary languages that should
     * be queried and REQUEST is the concrete query string that should be sent to metadict. See the concrete parameter
     * description for more information about the parameters.
     * This method invokes only a one-way query on the core and tries to resolve the internal dictionaries with
     * this preference.
     *
     * @param dictionaryString
     *         A comma-separated list of dictionaries to call. Each dictionary's language is separated with a minus
     *         ("-"). If you need to query a concrete dialect, use an underscore ("_") after the language identifiers.
     *         <p>
     *         Example: "de-en,de-no_ny" will issue a query from german to english (i.e. the two identifiers "de"
     *         and "en") and also german to norwegian nynorsk (i.e. the identifier "de" and the dialect "ny" of
     *         language "no").
     * @param queryRequest
     *         The concrete query string that should be passed to the internal engines. Special URI unescaping is
     *         handled automatically through the underlying JAX-RS engine.
     */
    @GET
    @Path("/uniquery/{dictionaries}/{request}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unidirectionalQuery(@PathParam("dictionaries") String dictionaryString, @PathParam("request") String queryRequest) {
        return internalExecuteQuery(dictionaryString, queryRequest, false);
    }

    private Response internalExecuteQuery(String dictionaryString, String queryRequest, boolean bidirectional) {
        List<Dictionary> dictionaries;
        try {
            dictionaries = DictionaryUtils.resolveDictionaries(dictionaryString, bidirectional);
        } catch (IllegalArgumentException e) {
            return Response.ok(new ResponseContainer<>(ResponseStatus.MALFORMED_QUERY, "Malformed dictionary query", null)).build();
        }

        if (dictionaries.size() == 0)
            return Response.ok(new ResponseContainer<>(ResponseStatus.ERROR, "No matching dictionaries found", null)).build();

        QueryResponse queryResponse;
        try {
            queryResponse =
                    metadictCore.createNewQueryRequestBuilder()
                            .setQueryString(queryRequest)
                            .setQueryDictionaries(dictionaries)
                            .build()
                            .executeRequest();
        } catch (Exception e) {
            LOGGER.error("An internal core error occurred", e);
            return Response.ok(new ResponseContainer<>(ResponseStatus.INTERNAL_ERROR, "An internal error occurred: " + e.getMessage(), null)).build();
        }

        return Response.ok(new ResponseContainer<>(ResponseStatus.OK, null, queryResponse)).build();
    }

}
