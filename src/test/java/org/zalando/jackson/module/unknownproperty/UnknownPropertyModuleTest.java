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

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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

    // TODO this is not working as expected, i.e. it will still instatiate the class
    @Test(expected = JsonMappingException.class)
    public void shouldLogUnknownPropertyWhenUsingCreator() throws IOException {
        final Logger logger = mock(Logger.class);
        final ObjectMapper mapper = register(logger);

        try {
            mapper.readValue(sample(), UnknownCreator.class);
        } finally {
            verify(logger).trace("Unknown property in {}: {}", UnknownCreator.class, "property");
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
    
    // TODO multiple handlers, one of which returns true

    private ObjectMapper register(Logger logger) {
        return new ObjectMapper()
                .registerModule(new UnknownPropertyModule(logger));
    }
    
    private URL sample() {
        return Resources.getResource("sample.json");
    }

}