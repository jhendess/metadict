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

package org.xlrnet.metadict.web.history.services;

import io.dropwizard.hibernate.UnitOfWork;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlrnet.metadict.core.api.query.QueryRequest;
import org.xlrnet.metadict.web.auth.entities.JwtPrincipal;
import org.xlrnet.metadict.web.auth.entities.PersistedUser;
import org.xlrnet.metadict.web.auth.services.UserService;
import org.xlrnet.metadict.web.history.dao.QueryLogAccess;
import org.xlrnet.metadict.web.history.entities.QueryLogEntry;
import org.xlrnet.metadict.web.middleware.services.SequenceService;

import javax.inject.Inject;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service which performs logging of search queries and allows them to be retrieved later on. Queries are only logged
 * for the currently authenticated user.
 */
public class QueryLoggingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryLoggingService.class);

    private final SequenceService sequenceService;

    private final UserService userService;

    private final QueryLogAccess queryLogAccess;

    @Inject
    public QueryLoggingService(SequenceService sequenceService, UserService userService, QueryLogAccess queryLogAccess) {
        this.sequenceService = sequenceService;
        this.userService = userService;
        this.queryLogAccess = queryLogAccess;
    }

    /**
     * Perform logging of the given query request if a user is currently authenticated.
     *
     * @param queryRequest
     *         The query request to log.
     */
    @UnitOfWork
    public boolean logQuery(@NotNull Optional<JwtPrincipal> principal, @NotNull QueryRequest queryRequest) {
        checkNotNull(queryRequest);
        boolean logged = false;

        if (principal.isPresent()) {
            Optional<PersistedUser> persistedUser = userService.findPersistedUserByName(principal.get().getName());
            if (persistedUser.isPresent()) {
                persistedUser.ifPresent(user -> logQuery(queryRequest, persistedUser.get()));
                logged = true;
            }
        } else {
            LOGGER.trace("Won't log query since no user is authenticated");
        }
        return logged;
    }

    /**
     * Perform the actual logging of the query.
     *
     * @param queryRequest
     *         The request to log.
     * @param user
     *         The user that performed the request.
     */
    private void logQuery(@NotNull QueryRequest queryRequest, @NotNull PersistedUser user) {
        String requestId = sequenceService.newUUIDString();
        QueryLogEntry queryLogEntry = new QueryLogEntry(requestId, queryRequest, user, Instant.now());
        queryLogAccess.persist(queryLogEntry);
    }

    /**
     * Returns the logged queries for the currently authenticated user.
     *
     * @param offset
     *         The offset for logged queries.
     * @param size
     *         The total size of queries to return. If zero, all entries will be returned.
     * @return A list containing the logged queries. If no user is currently authenticated, the list will be empty.
     */
    @NotNull
    @UnitOfWork
    public List<QueryLogEntry> getLoggedQueries(@NotNull Principal principal, int offset, int size) {
        checkArgument(offset >= 0, "Offset must be greater than zero or equal");
        checkArgument(size > 0, "Size must be greater than zero");

        List<QueryLogEntry> result;

        Optional<PersistedUser> user = userService.findPersistedUserByName(principal.getName());
        if (user.isPresent()) {
            result = readQueryLog(user.get(), offset, size);
        } else {
            LOGGER.warn("User {} doesn't exist", principal.getName());
            result = new ArrayList<>();
        }
        return result;
    }

    @NotNull
    private List<QueryLogEntry> readQueryLog(PersistedUser user, int offset, int size) {
        return queryLogAccess.findQueryLogEntriesPaged(user, offset, size);
    }
}
