package org.xlrnet.metadict.api.exception;

/**
 * Exception which indicates a misconfiguration.
 */
public class ConfigurationException extends MetadictRuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }
}
