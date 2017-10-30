package org.xlrnet.metadict.web.middleware.services;

import org.xlrnet.metadict.web.middleware.app.RequestContext;

/**
 * Dummy implementation of {@link RateControlService}. Disables limiting.
 */
public class DummyRateControlService extends RateControlService {

    @Override
    public boolean checkRateLimit(RequestContext requestContext) {
        return true;
    }
}
