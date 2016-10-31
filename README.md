# Metadict

Metadict is a modular meta search engine for dictionaries written in Java. It supports querying multiple
mono- and bilingual dictionary engines at the same time.
     
The default distribution in this repository contains backend with REST services, a responsive webapp and several search engines.
You can either use the core and its services in your own application or use the default backend that is distributed by default.

### Running

There is no need for an application server to run Metadict. To run Metadict, extract the distribution package and call:
  
```
java -jar metadict.jar server configuration-example.yaml
```

Afterwards, you can access the webapp at http://localhost:8080/

Note: prior to v0.4.0 Metadict was a .war archive that had to be deployed on a Wildfly application server. This is no longer the case.

### Configuration

You can look at configuration-example.yaml for further hints on what can be configured (primarily affecting storage at the moment).
Since Metadict runs on dropwizard, you can also configure everything that is described in the official [dropwizard documentation](http://www.dropwizard.io/1.0.2/docs/manual/core.html).
