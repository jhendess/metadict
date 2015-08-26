# Metadict

Metadict is a modular meta search engine for dictionaries written in Java. It supports querying multiple
mono- and bilingual dictionary engines at the same time. Metadict system is primarily based on CDI for detecting
available search engines and can be used in any CDI-based application.
     
The default distribution in this repository contains a REST service, a responsive webapp and several search engines.
Deployment of this configuration is primarily optimized for a Wildfly application server. A standalone version without
an external application server will follow. 

### Deployment

Metadict is being developed primarily to run on a Wildfly application server, but could also be modified to run on other
servers like Tomcat or Glassfish.

To deploy the application directly from maven on a running Wildfly instance, you can issue a call like this:

```
mvn clean package wildfly:deploy \
            -Dwildfly.username=[USERNAME] \
            -Dwildfly.password=[PASSWORD] \
            -Dmetadict-assembly.finalName=metadict-demo
```

Where `[USERNAME]` is the name of the deployment user on the target server and `[PASSWORD]` is the password of the deployment user.

### Configuration

The only thing that can be configured by now is which storage service Metadict will use. Storage services are currently
only used for allowing a persistent cache mechanism (i.e. cached search results are available after an application
restart). The default service is configured to be an inmemory HashMap which provides *no* actual persistence.

Configuring the default webapp distribution to use the persistent MapDB storage can be done by editing
the `storage.properties` file in the `metadict-assembly/properties/mapdb` folder. To build the default distribution with
this configuration, simply run the Maven build with "mapdb" profile by calling e.g.:

```
mvn clean package -P mapdb
```
