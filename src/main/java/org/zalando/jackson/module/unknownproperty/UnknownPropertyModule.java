package org.zalando.jackson.module.unknownproperty;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public final class UnknownPropertyModule extends Module {

    private static final Logger LOG = LoggerFactory.getLogger(UnknownPropertyModule.class);
    private static final Level LOG_LEVEL = Level.TRACE;

    private final Logger logger;
    private final Level logLevel;
    private final String format;

    public UnknownPropertyModule() {
        this(LOG);
    }

    public UnknownPropertyModule(final Logger logger) {
        this(logger, LOG_LEVEL);
    }

    public UnknownPropertyModule(final Level logLevel) {
        this(LOG, logLevel);
    }

    public UnknownPropertyModule(final String format) {
        this(LOG, LOG_LEVEL, format);
    }

    public UnknownPropertyModule(final Logger logger, final Level logLevel) {
        this(logger, logLevel, "Unknown property in {}: {}");
    }

    public UnknownPropertyModule(final Logger logger, final String format) {
        this(logger, LOG_LEVEL, format);
    }

    public UnknownPropertyModule(final Logger logger, final Level logLevel, final String format) {
        this.logger = logger;
        this.logLevel = logLevel;
        this.format = format;
    }

    @Override
    public String getModuleName() {
        return UnknownPropertyModule.class.getSimpleName();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Version version() {
        final ClassLoader loader = UnknownPropertyModule.class.getClassLoader();
        return VersionUtil.mavenVersionFor(loader, "org.zalando", "jackson-module-unknown-property");
    }

    @Override
    public void setupModule(final SetupContext context) {
        context.addDeserializationProblemHandler(new UnknownPropertyDeserializationProblemHandler(logger, logLevel, format));
    }

}
