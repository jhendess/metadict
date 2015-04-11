# Metadict
Metadict is a modular meta search engine for dictionaries written in Java.

Further information will come soon.


### Deployment

Metadict is being developed primarily to run on a Wildfly application server, but could also be modified to run on other servers like Tomcat or Glassfish.

To deploy the application directly from maven on a running Wildfly instance, you can issue a call like this:

```
mvn clean package wildfly:deploy \
            -Dwildfly.username=[USERNAME] \
            -Dwildfly.password=[PASSWORD] \
            -Dmetadict-web.warName=metadict \
            -Dwildfly.deployment.filename=metadict.war
```
Where `[USERNAME]` is the name of the deployment user on the target server and `[PASSWORD]` is the password of the deployment user.
