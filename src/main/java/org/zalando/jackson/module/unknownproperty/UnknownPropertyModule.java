package org.zalando.jackson.module.unknownproperty;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public final class UnknownPropertyModule extends Module {

    private static final Logger LOG = LoggerFactory.getLogger(UnknownPropertyModule.class);

    private final Logger logger;
    private final Level logLevel;
    private final String format;

    public UnknownPropertyModule() {
        this(LOG);
    }

    public UnknownPropertyModule(final Logger logger) {
        this(logger, "Unknown property in {}: {}");
    }

    public UnknownPropertyModule(final String format) {
        this(LOG, format);
    }

    public UnknownPropertyModule(final Logger logger, final String format) {
        this(logger, Level.TRACE, format);
    }

    private UnknownPropertyModule(final Logger logger, final Level logLevel, final String format) {
        this.logger = logger;
        this.logLevel = logLevel;
        this.format = format;
    }

    public UnknownPropertyModule withLogLevel(final Level logLevel) {
        return new UnknownPropertyModule(logger, logLevel, format);
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
