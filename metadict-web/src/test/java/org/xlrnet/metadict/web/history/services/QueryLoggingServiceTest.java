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
import org.xlrnet.metadict.api.auth.User;
import org.xlrnet.metadict.api.storage.StorageService;
import org.xlrnet.metadict.core.api.query.QueryRequest;
import org.xlrnet.metadict.core.services.storage.InMemoryStorage;
import org.xlrnet.metadict.web.auth.entities.BasicUser;
import org.xlrnet.metadict.web.auth.entities.JwtPrincipal;
import org.xlrnet.metadict.web.auth.services.UserService;
import org.xlrnet.metadict.web.history.entities.QueryLogEntry;
import org.xlrnet.metadict.web.middleware.services.SequenceService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test cases for {@link QueryLoggingService}.
 */
public class QueryLoggingServiceTest {

    private static final String TEST_USER_NAME = "test_user";

    private static final String TEST_USER_ID = "0";

    private QueryLoggingService queryLoggingService;

    private UserService userService;

    private StorageService storageService;

    @Before
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        storageService = spy(new InMemoryStorage());

        queryLoggingService = new QueryLoggingService(storageService, new SequenceService(), userService);
    }

    @Test
    public void logQuery_noUser() throws Exception {
        queryLoggingService.logQuery(Optional.empty(), mock(QueryRequest.class));
        verifyZeroInteractions(storageService, userService);
    }

    @Test
    public void logQuery_authenticated() throws Exception {
        JwtPrincipal principal = new JwtPrincipal(TEST_USER_NAME);
        User mockedUser = new BasicUser("0", TEST_USER_NAME, new TreeSet<>());
        when(userService.findUserDataByName(TEST_USER_NAME)).thenReturn(Optional.of(mockedUser));
        QueryRequest queryRequest = mock(QueryRequest.class);

        queryLoggingService.logQuery(Optional.of(principal), queryRequest);
        verify(storageService, times(2)).put(anyString(), anyString(), any());
    }

    @Test
    public void getLoggedQueries_none() throws Exception {
        JwtPrincipal principal = new JwtPrincipal(TEST_USER_NAME);
        User mockedUser = new BasicUser(TEST_USER_ID, TEST_USER_NAME, new TreeSet<>());
        when(userService.findUserDataByName(TEST_USER_NAME)).thenReturn(Optional.of(mockedUser));

        ArrayList<Object> list = new ArrayList<>();
        doReturn(Optional.of(list)).when(storageService).read(QueryLoggingService.QUERY_LOG_NAMESPACE, TEST_USER_ID, ArrayList.class);

        List<QueryLogEntry> loggedQueries = queryLoggingService.getLoggedQueries(principal, 0, 10);
        assertTrue("Query log must be empty", loggedQueries.isEmpty());
    }

    @Test
    public void getLoggedQueries_two() throws Exception {
        JwtPrincipal principal = new JwtPrincipal(TEST_USER_NAME);
        User mockedUser = new BasicUser(TEST_USER_ID, TEST_USER_NAME, new TreeSet<>());
        when(userService.findUserDataByName(TEST_USER_NAME)).thenReturn(Optional.of(mockedUser));

        // Setup stored query requests
        QueryRequest queryRequest = mock(QueryRequest.class);
        QueryLogEntry queryLogEntry1 = new QueryLogEntry("entry1", queryRequest, Instant.now());
        QueryLogEntry queryLogEntry2 = new QueryLogEntry("entry2", queryRequest, Instant.now());

        doReturn(Optional.of(queryLogEntry1)).when(storageService).read(QueryLoggingService.QUERY_LOG_ENTRIES_NAMESPACE, "entry1", QueryLogEntry.class);
        doReturn(Optional.of(queryLogEntry2)).when(storageService).read(QueryLoggingService.QUERY_LOG_ENTRIES_NAMESPACE, "entry2", QueryLogEntry.class);

        ArrayList<Object> list = new ArrayList<>();
        list.add("entry1");
        list.add("entry2");
        doReturn(Optional.of(list)).when(storageService).read(QueryLoggingService.QUERY_LOG_NAMESPACE, TEST_USER_ID, ArrayList.class);

        List<QueryLogEntry> loggedQueries = queryLoggingService.getLoggedQueries(principal, 0, 10);

        assertFalse("Query log may not be empty", loggedQueries.isEmpty());
        assertEquals(list.size(), loggedQueries.size());

        assertTrue("Log entry 1 is missing", loggedQueries.contains(queryLogEntry1));
        assertTrue("Log entry 2 is missing", loggedQueries.contains(queryLogEntry2));
    }
}