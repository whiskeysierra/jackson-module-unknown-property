package org.zalando.jackson.module.unknownproperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("ignored")
final class PartiallyIgnored {

}
