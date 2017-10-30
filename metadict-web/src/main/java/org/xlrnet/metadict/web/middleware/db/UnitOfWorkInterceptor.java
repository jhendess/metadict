package org.xlrnet.metadict.web.middleware.db;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provider;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.hibernate.UnitOfWorkAspect;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Custom guice interceptor which provides support for {@link UnitOfWork} outside of jersey resources.
 */
public class UnitOfWorkInterceptor implements MethodInterceptor {

    private final Provider<SessionFactory> sessionFactoryProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitOfWorkInterceptor.class);

    @Inject
    public UnitOfWorkInterceptor(Provider<SessionFactory> sessionFactory) {
        this.sessionFactoryProvider = sessionFactory;
        LOGGER.debug("Created new transaction interceptor");
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        UnitOfWork unitOfWork = invocation.getMethod().getAnnotation(UnitOfWork.class);
        SessionFactory sessionFactory = sessionFactoryProvider.get();
        UnitOfWorkAspect aspect = new UnitOfWorkAspect(ImmutableMap.of("", sessionFactory));
        Object result;

        try {
            LOGGER.trace("Starting unit of work for {}", invocation.getMethod().toString());
            aspect.beforeStart(unitOfWork);
            result = invocation.proceed();
            aspect.afterEnd();
        } catch (Exception e) {
            aspect.onError();
            LOGGER.trace("Unexpected exception while processing unit of work {}", invocation.getMethod().toString(), e);
            throw e;
        } finally {
            aspect.onFinish();
            LOGGER.trace("Finished unit of work {}", invocation.getMethod().toString());
        }
        return result;

    }
}
