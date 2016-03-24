package org.zalando.jackson.module.unknownproperty;

/*
 * ⁣​
 * Jackson-module-Unknown-Property
 * ⁣⁣
 * Copyright (C) 2015 - 2016 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fasterxml.jackson.core.util.VersionUtil.mavenVersionFor;

public final class UnknownPropertyModule extends Module {

    private static final Logger LOG = LoggerFactory.getLogger(UnknownPropertyModule.class);

    private final Logger logger;
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
        this.logger = logger;
        this.format = format;
    }

    @Override
    public String getModuleName() {
        return UnknownPropertyModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        final ClassLoader loader = UnknownPropertyModule.class.getClassLoader();
        return mavenVersionFor(loader, "org.zalando", "jackson-module-unknown-property");
    }

    @Override
    public void setupModule(final SetupContext context) {
        context.addDeserializationProblemHandler(new UnknownPropertyDeserializationProblemHandler(logger, format));
    }

}
