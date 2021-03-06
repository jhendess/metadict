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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.core.services.query.EngineRegistryService;
import org.xlrnet.metadict.web.api.ResponseContainer;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST service for querying various kinds of information about the available dictionaries.
 * <p>
 * This service has three main endpoints for querying the available dictionaries itself:
 * <ul>
 * <li>All dictionaries: /api/dict/all returns a list of all available dictionaries</li>
 * <li>Both-way dictionaries: /api/dict/bi returns a list of all bidirected dictionaries, where each combination of
 * languages
 * will appear only one time regardless of their order. E.g.: If the dictionaries de-en and en-de exist, only de-en
 * will
 * be returned.</li>
 * <li>One-way dictionaries: /api/dict/uni returns a list of all dictionaries that can be queried in one direction
 * only.</li>
 * </ul>
 */
@Path("/dictionaries")
public class DictionaryResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryResource.class);

    /** The central engine registry. */
    private EngineRegistryService engineRegistryService;

    public DictionaryResource() {
    }

    @Inject
    public DictionaryResource(EngineRegistryService engineRegistryService) {
        this.engineRegistryService = engineRegistryService;
    }

    @GET
    @Path("/bilingual")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listBilingualDictionaries() {
        return Response.ok(ResponseContainer.fromSuccessful(this.engineRegistryService.getSupportedDictionaries())).build();
    }

    @GET
    @Path("/bilingual/bidirected")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listBidirectedRegisteredDictionaries() {
        return Response.ok(ResponseContainer.fromSuccessful(getBidirectedDictionaries())).build();
    }

    @GET
    @Path("/bilingual/unidirected")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUnidirectedRegisteredDictionaries() {
        return Response.ok(ResponseContainer.fromSuccessful(getUnidirectedDictionaries())).build();
    }

    @GET
    @Path("/monolingual")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listMonolingualDictionaries() {
        // FIXME: These are actually not monolingual
        return Response.ok(ResponseContainer.fromSuccessful(getUnidirectedDictionaries())).build();
    }

    @NotNull
    private Collection<BilingualDictionary> getBidirectedDictionaries() {
        List<BilingualDictionary> bidirectional = this.engineRegistryService
                .getSupportedDictionaries()
                .stream()
                .filter(BilingualDictionary::isBidirectional)
                .distinct()
                .collect(Collectors.toList());

        List<BilingualDictionary> distinctBidirectional = new ArrayList<>(bidirectional.size());
        for (BilingualDictionary bilingualDictionary : bidirectional) {
            if (!distinctBidirectional.contains(BilingualDictionary.inverse(bilingualDictionary))) {
                distinctBidirectional.add(bilingualDictionary);
            }
        }
        return distinctBidirectional;
    }

    @NotNull
    private Collection<BilingualDictionary> getUnidirectedDictionaries() {
        return this.engineRegistryService
                .getSupportedDictionaries()
                .stream()
                .filter(d -> !d.isBidirectional())
                .collect(Collectors.toList());
    }


}