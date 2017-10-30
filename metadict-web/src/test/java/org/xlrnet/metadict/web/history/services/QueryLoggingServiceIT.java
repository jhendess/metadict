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

import org.junit.Before;
import org.junit.Test;
import org.xlrnet.metadict.api.language.BilingualDictionary;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.core.api.query.QueryRequest;
import org.xlrnet.metadict.core.services.aggregation.group.GroupingType;
import org.xlrnet.metadict.core.services.aggregation.order.OrderType;
import org.xlrnet.metadict.core.services.query.QueryRequestBuilder;
import org.xlrnet.metadict.web.AbstractIT;
import org.xlrnet.metadict.web.auth.entities.JwtPrincipal;
import org.xlrnet.metadict.web.auth.services.UserService;
import org.xlrnet.metadict.web.history.entities.QueryLogEntry;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Test cases for {@link QueryLoggingService}.
 */
public class QueryLoggingServiceIT extends AbstractIT {

    private static final String TEST_USER_NAME = "test_user";

    private static final String QUERY_STRING = "testQuery";

    private QueryLoggingService queryLoggingService;

    private UserService userService;

    @Before
    public void setUp() throws Exception {
        queryLoggingService = getBean(QueryLoggingService.class);
        userService = getBean(UserService.class);
    }

    @Test
    public void logQuery_noUser() throws Exception {
        boolean logResult = queryLoggingService.logQuery(Optional.empty(), mock(QueryRequest.class));
        assertFalse(logResult);
    }

    @Test
    public void logQuery_authenticated() throws Exception {
        runAsUnitOfWork(() -> userService.createNewUser(TEST_USER_NAME, "password"));

        JwtPrincipal principal = new JwtPrincipal(TEST_USER_NAME);
        BilingualDictionary dictionary1 = BilingualDictionary.fromQueryString("de-en");
        BilingualDictionary dictionary2 = BilingualDictionary.fromQueryString("fr_ca-en_us");
        QueryRequest queryRequest = new QueryRequestBuilder()
                .setQueryString(QUERY_STRING)
                .addQueryDictionary(dictionary1)
                .addQueryDictionary(dictionary2)
                .addQueryLanguage(Language.ENGLISH)
                .setGroupBy(GroupingType.DICTIONARY)
                .setOrderBy(OrderType.RELEVANCE).build();

        boolean result = queryLoggingService.logQuery(Optional.of(principal), queryRequest);
        assertTrue(result);

        List<QueryLogEntry> loggedQueries = queryLoggingService.getLoggedQueries(principal, 0, 10);
        assertEquals(1, loggedQueries.size());

        QueryLogEntry queryLogEntry = loggedQueries.get(0);
        assertEquals(QUERY_STRING, queryLogEntry.getQueryString());
        assertEquals(GroupingType.DICTIONARY, queryLogEntry.getGroupingType());
        assertEquals(OrderType.RELEVANCE, queryLogEntry.getOrderType());

        assertEquals(2, queryLogEntry.getBilingualDictionaries().size());
        assertTrue(queryLogEntry.getBilingualDictionaries().contains(dictionary1));
        assertTrue(queryLogEntry.getBilingualDictionaries().contains(dictionary2));
        assertEquals(1, queryLogEntry.getMonolingualLanguages().size());
        assertTrue(queryLogEntry.getMonolingualLanguages().contains(Language.ENGLISH));
    }
}