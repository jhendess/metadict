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

import com.google.common.base.Enums;
import io.dropwizard.auth.Auth;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.UnsupportedDictionaryException;
import org.xlrnet.metadict.core.api.query.QueryRequest;
import org.xlrnet.metadict.core.api.query.QueryResponse;
import org.xlrnet.metadict.core.services.aggregation.group.GroupingType;
import org.xlrnet.metadict.core.services.aggregation.order.OrderType;
import org.xlrnet.metadict.core.services.query.QueryService;
import org.xlrnet.metadict.core.util.BilingualDictionaryUtils;
import org.xlrnet.metadict.web.api.ResponseContainer;
import org.xlrnet.metadict.web.api.ResponseStatus;
import org.xlrnet.metadict.web.auth.entities.JwtPrincipal;
import org.xlrnet.metadict.web.history.services.QueryLoggingService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * REST service for sending a query to the Metadict core.
 * <p/>
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
public class QueryResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryResource.class);

    /** Injected query service. */
    private final QueryService queryService;

    /** Injected query logging service. Stores all queries performed by a user. */
    private final QueryLoggingService queryLoggingService;

    @Inject
    public QueryResource(QueryService queryService, QueryLoggingService queryLoggingService) {
        this.queryService = queryService;
        this.queryLoggingService = queryLoggingService;
    }

    /**
     * Issue a two-way dictionary query.
     * <p/>
     * Endpoint: /api/query/{DICTIONARIES}/{REQUEST}?groupBy={GROUPING}&orderBy={ORDERING} where DICTIONARIES is
     * the list of dictionary languages that should be queried and REQUEST is the concrete query string that should be
     * sent to metadict. Optional parameters {GROUPING} and {ORDERING} can be used to define how the results should be
     * grouped and ordered. See the concrete parameter description for more information about the parameters.
     * This method invokes an automatic two-way query on the core and tries to resolve the internal dictionaries with
     * this preference. This is usually the preferred way for querying.
     *
     * @param dictionaryString
     *         A comma-separated list of dictionaries to call. Each dictionary's language is separated with a minus
     *         ("-"). If you need to query a concrete dialect, use an underscore ("_") after the language identifiers.
     *         <p/>
     *         Example: "de-en,de-no_ny" will issue a query between german and english (i.e. the two identifiers "de"
     *         and "en") and also german and norwegian nynorsk (i.e. the identifier "de" and the dialect "ny" of
     *         language "no").
     * @param queryRequest
     *         The concrete query string that should be passed to the internal engines. Special URI unescaping is
     *         handled automatically through the underlying JAX-RS engine.
     * @param grouping
     *         Define how the resulting entries should be grouped by metadict. This string value has to correspond with
     *         one of the constants defined in {@link GroupingType} but will only be checked case-insensitive. If no
     *         value is defined, the single-group strategy will be used.
     * @param ordering
     *         Define how the resulting entry groups should be ordered by metadict. This string value has to correspond
     *         with one of the constants defined in {@link OrderType} but will only be checked case-insensitive. If no
     *         value is defined, the relevance ordering will be used.
     */
    @GET
    @Path("/query/{dictionaries}/{request}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response bidirectionalQuery(@Auth Optional<JwtPrincipal> principal, @PathParam("dictionaries") String dictionaryString, @PathParam("request") String queryRequest, @QueryParam("groupBy") String grouping, @QueryParam("orderBy") String ordering) {
        return internalExecuteQuery(principal, dictionaryString, queryRequest, grouping, ordering, true);
    }

    /**
     * Issue a one-way dictionary query.
     * <p/>
     * Endpoint: /api/uniquery/{DICTIONARIES}/{REQUEST}?groupBy={GROUPING}&orderBy={ORDERING} where DICTIONARIES is
     * the list of dictionary languages that should be queried and REQUEST is the concrete query string that should be
     * sent to metadict. Optional parameters {GROUPING} and {ORDERING} can be used to define how the results should be
     * grouped and ordered. See the concrete parameter description for more information about the parameters.
     * This method invokes only a one-way query on the core and tries to resolve the internal dictionaries with
     * this preference.
     * <p/>
     * Error situations:
     * <ul>
     * <li>If an unknown dictionary was requested, a 404 response will be returned.</li>
     * <li>If the dictionary query is invalid, a 400 response will be returned.</li>
     * </ul>
     *
     * @param dictionaryString
     *         A comma-separated list of dictionaries to call. Each dictionary's language is separated with a minus
     *         ("-"). If you need to query a concrete dialect, use an underscore ("_") after the language identifiers.
     *         <p/>
     *         Example: "de-en,de-no_ny" will issue a query from german to english (i.e. the two identifiers "de" and
     *         "en") and also german to norwegian nynorsk (i.e. the identifier "de" and the dialect "ny" of language
     *         "no").
     * @param queryString
     *         The concrete query string that should be passed to the internal engines. Special URI unescaping is
     *         handled automatically through the underlying JAX-RS engine.
     * @param grouping
     *         Define how the resulting entries should be grouped by metadict. This string value has to correspond with
     *         one of the constants defined in {@link GroupingType} but will only be checked case-insensitive. If no
     *         value is defined, the single-group strategy will be used.
     * @param ordering
     *         Define how the resulting entry groups should be ordered by metadict. This string value has to correspond
     *         with one of the constants defined in {@link OrderType} but will only be checked case-insensitive. If no
     *         value is defined, the relevance ordering will be used.
     */
    @GET
    @Path("/uniquery/{dictionaries}/{request}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unidirectionalQuery(@Auth Optional<JwtPrincipal> principal, @PathParam("dictionaries") String dictionaryString, @PathParam("request") String queryString, @QueryParam("groupBy") String grouping, @QueryParam("orderBy") String ordering) {
        return internalExecuteQuery(principal, dictionaryString, queryString, grouping, ordering, false);
    }

    /**
     * Resolve dictionaries and finally execute the query.
     */
    private Response internalExecuteQuery(@Auth Optional<JwtPrincipal> principal, @NotNull String dictionaryString, @NotNull String queryString, @Nullable String grouping, @Nullable String ordering, boolean bidirectional) {
        GroupingType groupingType = Enums.getIfPresent(GroupingType.class, StringUtils.stripToEmpty(grouping).toUpperCase()).or(GroupingType.NONE);
        OrderType orderType = Enums.getIfPresent(OrderType.class, StringUtils.stripToEmpty(ordering).toUpperCase()).or(OrderType.RELEVANCE);
        List<BilingualDictionary> dictionaries;
        try {
            dictionaries = BilingualDictionaryUtils.resolveDictionaries(dictionaryString, bidirectional);
        } catch (IllegalArgumentException e) {    // NOSONAR: Logging not necessary
            return Response.status(Response.Status.BAD_REQUEST).entity(new ResponseContainer<>(ResponseStatus.MALFORMED_QUERY, "Malformed dictionary query", null)).build();
        } catch (UnsupportedDictionaryException e) {    // NOSONAR: Logging not necessary
            return Response.status(Response.Status.NOT_FOUND).entity(new ResponseContainer<>(ResponseStatus.ERROR, "Unsupported dictionary", null)).build();
        }

        if (dictionaries.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ResponseContainer<>(ResponseStatus.ERROR, "No matching dictionaries found", null)).build();
        }

        QueryRequest queryRequest = this.queryService.createNewQueryRequestBuilder()
                .setQueryString(queryString)
                .setQueryDictionaries(dictionaries)
                .setAutoDeriveMonolingualLanguages(true)
                .setGroupBy(groupingType)
                .setOrderBy(orderType)
                .build();

        if (principal.isPresent()) {
            queryLoggingService.logQuery(principal, queryRequest);
        }

        QueryResponse queryResponse = this.queryService.executeQuery(queryRequest);

        return Response.ok(new ResponseContainer<>(ResponseStatus.OK, null, queryResponse)).build();
    }

}
