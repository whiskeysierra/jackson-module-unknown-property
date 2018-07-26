package org.zalando.jackson.module.unknownproperty;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.slf4j.Logger;
import org.slf4j.event.Level;

final class UnknownPropertyDeserializationProblemHandler extends DeserializationProblemHandler {

    private final Logger logger;
    private final Level logLevel;
    private final String format;

    UnknownPropertyDeserializationProblemHandler(final Logger logger, final Level logLevel, final String format) {
        this.logger = logger;
        this.logLevel = logLevel;
        this.format = format;
    }

    @Override
    public boolean handleUnknownProperty(final DeserializationContext context, final JsonParser parser,
            final JsonDeserializer<?> deserializer, final Object beanOrClass, final String propertyName) {

        // TODO based on the documentation this could be a class already, but I couldn't figure out when this happens
        final Class<?> type = beanOrClass.getClass();

        switch (logLevel) {
            case DEBUG:
                logger.debug(format, type, propertyName);
                break;
            case INFO:
                logger.info(format, type, propertyName);
                break;
            case WARN:
                logger.warn(format, type, propertyName);
                break;
            case ERROR:
                logger.error(format, type, propertyName);
                break;
            case TRACE:
            default:
                logger.trace(format, type, propertyName);
                break;
        }

        return false;
    }

}
