package org.zalando.jackson.module.unknownproperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("property")
final class Ignored {

}
