package org.zalando.jackson.module.unknownproperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
final class IgnoredUnknown {

}
