package org.xlrnet.metadict.web.resources;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.xlrnet.metadict.web.AbstractIT;
import org.xlrnet.metadict.web.api.ResponseContainer;
import org.xlrnet.metadict.web.history.entities.QueryLogEntry;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Perform test on the query resource.
 */
public class QueryResourceIT extends AbstractIT {

    /**
     * Number of characters which is too long.
     */
    private static final int TOO_MANY_CHARACTERS = 201;

    @Test
    public void testRequestTooLong_bilingual() {
        Invocation invocation = getTarget().path("/query/de-en/" + RandomStringUtils.randomAlphabetic(TOO_MANY_CHARACTERS)).request().buildGet();
        Response response = invocation.invoke();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testLogRequest_authenticatedHistoryLogging() {
        NewCookie sessionCookie = registerAndLogin();
        getTarget().path("/query/demo-test/sampleQuery").request().cookie(sessionCookie).buildGet().invoke();

        ResponseContainer<List<QueryLogEntry>> responseContainer = getTarget().path("/history").request().cookie(sessionCookie).buildGet().invoke(ResponseContainer.class);
        assertNotNull(responseContainer.getData());
        assertFalse(responseContainer.getData().isEmpty());
    }
}
