package org.zalando.jackson.module.unknownproperty;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public final class UnknownPropertyModuleTest {

    @Test
    public void shouldSupportSpi() {
        final List<Module> modules = ObjectMapper.findModules();
        assertThat(modules, hasItem(instanceOf(UnknownPropertyModule.class)));
    }

    @Test
    public void shouldNotLogKnownProperty() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = register(logger);

        mapper.readValue(sample(), Known.class);

        verifyNoMoreInteractions(logger);
    }

    @Test(expected = JsonMappingException.class)
    public void shouldLogUnknownProperty() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = register(logger);

        try {
            mapper.readValue(sample(), Unknown.class);
        } finally {
            verify(logger).trace("Unknown property in {}: {}", Unknown.class, "property");
        }
    }

    @Test(expected = JsonMappingException.class)
    public void shouldLogPartiallyIgnoredUnknownProperty() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = register(logger);

        try {
            mapper.readValue(sample(), PartiallyIgnored.class);
        } finally {
            verify(logger).trace("Unknown property in {}: {}", PartiallyIgnored.class, "property");
        }
    }

    @Test
    public void shouldLogUnknownPropertyEvenIfFailOnUnknownPropertiesIsGloballyDisabled() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = register(logger).disable(FAIL_ON_UNKNOWN_PROPERTIES);

        mapper.readValue(sample(), Unknown.class);
        verify(logger).trace("Unknown property in {}: {}", Unknown.class, "property");
    }

    @Test
    public void shouldNotLogHandledUnknownProperty() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new UnknownPropertyModule(logger))
                .addHandler(alwaysRepondWith(true));

        mapper.readValue(sample(), Unknown.class);

        verifyNoMoreInteractions(logger);
    }

    @Test(expected = JsonMappingException.class)
    public void shouldLogPreviouslyUnhandledUnknownProperty() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new UnknownPropertyModule(logger))
                .addHandler(alwaysRepondWith(false));

        try {
            mapper.readValue(sample(), PartiallyIgnored.class);
        } finally {
            verify(logger).trace("Unknown property in {}: {}", PartiallyIgnored.class, "property");
        }
    }

    @Test
    public void shouldLogUnhandledPropertyAsFirstHandler() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = new ObjectMapper()
                .addHandler(alwaysRepondWith(true))
                .registerModule(new UnknownPropertyModule(logger));

        mapper.readValue(sample(), PartiallyIgnored.class);
        verify(logger).trace("Unknown property in {}: {}", PartiallyIgnored.class, "property");
    }

    @Test
    public void shouldRespectErrorLogLevel() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = new ObjectMapper()
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new UnknownPropertyModule(logger).withLogLevel(Level.ERROR));

        mapper.readValue(sample(), PartiallyIgnored.class);
        verify(logger).error("Unknown property in {}: {}", PartiallyIgnored.class, "property");
    }

    @Test
    public void shouldRespectWarnLogLevel() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = new ObjectMapper()
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new UnknownPropertyModule(logger).withLogLevel(Level.WARN));

        mapper.readValue(sample(), PartiallyIgnored.class);
        verify(logger).warn("Unknown property in {}: {}", PartiallyIgnored.class, "property");
    }

    @Test
    public void shouldRespectDebugLogLevel() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = new ObjectMapper()
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new UnknownPropertyModule(logger).withLogLevel(Level.DEBUG));

        mapper.readValue(sample(), PartiallyIgnored.class);
        verify(logger).debug("Unknown property in {}: {}", PartiallyIgnored.class, "property");
    }

    @Test
    public void shouldRespectInfoLogLevel() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = new ObjectMapper()
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new UnknownPropertyModule(logger).withLogLevel(Level.INFO));

        mapper.readValue(sample(), PartiallyIgnored.class);
        verify(logger).info("Unknown property in {}: {}", PartiallyIgnored.class, "property");
    }

    private DeserializationProblemHandler alwaysRepondWith(final boolean value) {
        return new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(final DeserializationContext context, final JsonParser parser,
                    final JsonDeserializer<?> deserializer, final Object beanOrClass,
                    final String propertyName) {
                return value;
            }
        };
    }

    @Test(expected = JsonMappingException.class)
    public void shouldLogUnknownPropertyWithCustomFormat() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new UnknownPropertyModule(logger,
                        "Well this is odd... somebody changed {} and added '{}'"));

        try {
            mapper.readValue(sample(), Unknown.class);
        } finally {
            verify(logger).trace("Well this is odd... somebody changed {} and added '{}'", Unknown.class, "property");
        }
    }

    @Test
    public void shouldNotLogIgnoredProperty() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = register(logger);

        mapper.readValue(sample(), Ignored.class);

        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldNotLogIgnoredUnknownProperty() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = register(logger);

        mapper.readValue(sample(), IgnoredUnknown.class);

        verifyNoMoreInteractions(logger);
    }

    private ObjectMapper register(Logger logger) {
        return new ObjectMapper()
                .registerModule(new UnknownPropertyModule(logger));
    }

    private static URL sample() {
        return from("sample.json");
    }

    private static URL from(String resourceName) {
        return Resources.getResource(resourceName);
    }

}
