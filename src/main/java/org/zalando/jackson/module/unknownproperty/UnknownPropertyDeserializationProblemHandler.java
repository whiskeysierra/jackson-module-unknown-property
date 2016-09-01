package org.zalando.jackson.module.unknownproperty;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.slf4j.Logger;

final class UnknownPropertyDeserializationProblemHandler extends DeserializationProblemHandler {
    
    private final Logger logger;
    private final String format;

    UnknownPropertyDeserializationProblemHandler(final Logger logger, final String format) {
        this.logger = logger;
        this.format = format;
    }

    @Override
    public boolean handleUnknownProperty(final DeserializationContext context, final JsonParser parser, 
            final JsonDeserializer<?> deserializer, final Object beanOrClass, final String propertyName) {
        
        // TODO based on the documentation this could be a class already, but I couldn't figure out when this happens
        final Class<?> type = beanOrClass.getClass();
        logger.trace(format, type, propertyName);
        
        return false;
    }

}
