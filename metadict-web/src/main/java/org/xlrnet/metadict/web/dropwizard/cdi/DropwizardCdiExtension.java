package org.xlrnet.metadict.web.dropwizard.cdi;

import com.google.common.collect.Sets;
import org.slf4j.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

class DropwizardCdiExtension implements Extension {

    private final Logger logger = getLogger(this.getClass());

    private final Set<String> names = Sets.newHashSet();

    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
        logger.error("============================> beginning the scanning process");
    }

    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        final String name = pat.getAnnotatedType().getJavaClass().getName();
        logger.debug("============================> scanning type: {}", name);
        names.add(name);
    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
        logger.error("============================> finished the scanning process");
    }

    public Set<String> getNames() {
        return names;
    }
}
