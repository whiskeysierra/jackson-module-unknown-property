# Jackson Module Unknown Property

[![Stability: Maintenance](https://masterminds.github.io/stability/maintenance.svg)](https://masterminds.github.io/stability/maintenance.html)
[![Build Status](https://img.shields.io/travis/whiskeysierra/jackson-module-unknown-property/master.svg)](https://travis-ci.org/whiskeysierra/jackson-module-unknown-property)
[![Coverage Status](https://img.shields.io/coveralls/whiskeysierra/jackson-module-unknown-property/master.svg)](https://coveralls.io/r/whiskeysierra/jackson-module-unknown-property)
[![Code Quality](https://img.shields.io/codacy/grade/4ba764808bae4a81bead924eaacb829e/master.svg)](https://www.codacy.com/app/whiskeysierra/jackson-module-unknown-property)
[![Javadoc](http://javadoc.io/badge/org.zalando/jackson-module-unknown-property.svg)](http://www.javadoc.io/doc/org.zalando/jackson-module-unknown-property)
[![Release](https://img.shields.io/github/release/whiskeysierra/jackson-module-unknown-property.svg)](https://github.com/whiskeysierra/jackson-module-unknown-property/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/jackson-module-unknown-property.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/jackson-module-unknown-property)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/whiskeysierra/jackson-module-unknown-property/master/LICENSE)

*Unknown Property* is a [Jackson](https://github.com/codehaus/jackson) extension module that adds standardized logging 
of unknown properties.

Consumers of RESTful APIs should be resilient to changes, most importantly they shouldn't break when a server sends
a new, unknown property. The goal of this module is to let clients know that a new property exists, so they can decide
to either ignore it explicitly or to use it, in case it proves to be useful.

## Features

- log new, unknown properties in JSON messages as soon as they appear
- increases awareness of API changes on consumer side

## Dependencies

- Java 8
- Any build tool using Maven Central, or direct download
- Jackson
- SLF4J

## Installation

Add the following dependency to your project:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>jackson-module-unknown-property</artifactId>
    <version>${jackson-module-unknown-property.version}</version>
</dependency>
```

## Configuration

Register the module with your `ObjectMapper`:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new UnknownPropertyModule());
```

Alternatively, you can use the SPI capabilities:

```java
ObjectMapper mapper = new ObjectMapper()
    .findAndRegisterModules();
```

Typically you will disable the `FAIL_ON_UNKNOWN_PROPERTIES` feature, as it contradicts the whole idea of being a
resilient API client:

```java
ObjectMapper mapper = new ObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .registerModule(new UnknownPropertyModule());
```

**Beware** this module is implemented as a `DeserializationProblemHandler`. If you register multiple handlers they
are running in reverse order, i.e. the handler that is registered last will run first.

### Customization

The logging category defaults to `org.zalando.jackson.module.unknownproperty.UnknownPropertyModule` but can be
customized by passing a logger: 

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new UnknownPropertyModule(LoggerFactory.getLogger("unknown-property")));
```

The logging format defaults to `Unknown property in {}: {}` but can also be customized:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new UnknownPropertyModule("Well this is odd... somebody changed {} and added '{}'"));
```

Please note that the first parameter is the type and the second one is the property name.

The log level defaults to `TRACE` but can also be customized:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new UnknownPropertyModule(Level.INFO));
```

## Usage

After configuring the module when for example the following JSON ...

```json
{
  "name": "Alice",
  "age": 31
}
```

... is deserialized into the following class ...

```java
public class Person {
    private String name;
    ...
}
```

... then, depending on the underlying logging framework, the entry in the logfile may look like this:

```
2016-03-24T09:33:13 [main] TRACE UnknownPropertyModule - Unknown property in class Person: age
```

To suppress the warning you just explicitly ignore the property:

```java
@JsonIgnoreProperties("age")
public class Person {
    ...
}
```

## Getting help

If you have questions, concerns, bug reports, etc, please file an issue in this repository's Issue Tracker.

## Getting involved

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details check the [contribution guidelines](.github/CONTRIBUTING.md).
