# Jackson Module Unknown Property

[![Build Status](https://img.shields.io/travis/zalando/jackson-module-unknown-property.svg)](https://travis-ci.org/zalando/jackson-module-unknown-property)
[![Coverage Status](https://img.shields.io/coveralls/zalando/jackson-module-unknown-property.svg)](https://coveralls.io/r/zalando/jackson-module-unknown-property)
[![Release](https://img.shields.io/github/release/zalando/jackson-module-unknown-property.svg)](https://github.com/zalando/jackson-module-unknown-property/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/jackson-module-unknown-property.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/jackson-module-unknown-property)

*Jackson Module Unknown Property* is a [Jackson](https://github.com/codehaus/jackson) extension module that adds 
standardized logging of unknown properties.

RESTful API clients/consumers should be resilient to changes, most importantly they shouldn't break when a server sends
a new, unknown property. The goal of this module is to let clients know that a new property exists, so they can decide
to either ignore it explicitely or to use it, in case it's useful for them.

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

The logging category defaults to `org.zalando.jackson.module.unknownproperty.UnknownPropertyModule` but can be
customized by passing a logger: 

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new UnknownPropertyModule(LoggerFactory.getLogger("unknown-property")));
```

The logging format defaults to `Unknown property in class {}: {}` but can also be customized by passing overriding it:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new UnknownPropertyModule("Well this is odd... somebody changed {} and added '{}'"));
```

Please note that the first parameter is the type and the second one is the property name.

The log level is `TRACE` and as for now it's not configurable.

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
2016-03-24T09:33:13 [thread-1] TRACE o.z.j.m.u.UnknownPropertyModule - Unknown property in class Person: age
```

## Getting help

If you have questions, concerns, bug reports, etc, please file an issue in this repository's Issue Tracker.

## Getting involved

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details check the [contribution guidelines](CONTRIBUTING.md).