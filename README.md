# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> Persistence components for Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit. It contains generic interfaces for data access components as well as abstract implementations for in-memory and file persistence.

The persistence components come in two kinds. The first kind is a basic persistence that can work with any object types and provides only minimal set of operations. 
The second kind is so called "identifieable" persistence with works with "identifable" data objects, i.e. objects that have unique ID field. The identifiable persistence provides a full set or CRUD operations that covers most common cases.

The module contains the following packages:
- **Core** - generic interfaces for data access components. 
- **Persistence** - in-memory and file persistence components, as well as JSON persister class.

<a name="links"></a> Quick links:

* [Memory persistence](http://docs.pipservices.org/toolkit/recipes/memory_persistence/)
* [API Reference](https://pip-services3-java.github.io/pip-services3-data-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices3</groupId>
  <artifactId>pip-services3-data</artifactId>
  <version>3.1.0</version>
</dependency>
```

As an example, lets implement persistence for the following data object.

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pipservices3.commons.data.IStringIdentifiable;

public class MyObject implements IStringIdentifiable {
    public MyObject() {}

    public MyObject(String id, String key, String content) {
        super();
        this._id = id;
        this._key = key;
        this._content = content;
    }


    @JsonProperty("id")
    private String _id;
    public String getId() {	return _id; }
    public void setId(String id) {	this._id = id;}

    @JsonProperty("key")
    private String _key;
    public String getKey() { return _key; }
    public void setKey(String key) { this._key = key; }

    @JsonProperty("content")
    private String _content;
    public String getContent() { return _content; }
    public void setContent(String content) { this._content = content; }
}

```

Our persistence component shall implement the following interface with a basic set of CRUD operations.

```java
import org.pipservices3.commons.data.DataPage;
import org.pipservices3.commons.data.FilterParams;
import org.pipservices3.commons.data.PagingParams;
import org.pipservices3.commons.errors.ApplicationException;


public interface IMyPersistence {
    DataPage<Dummy> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging) throws ApplicationException;
    Dummy getOneById(String correlationId, String dummyId) throws ApplicationException;
    Dummy getOneByKey(String correlationId, String key) throws ApplicationException;
    Dummy create(String correlationId, Dummy dummy) throws ApplicationException;
    Dummy update(String correlationId, Dummy dummy) throws ApplicationException;

    Dummy deleteById(String correlationId, String dummyId) throws ApplicationException;
}
```

To implement in-memory persistence component you shall inherit `IdentifiableMemoryPersistence`.
Most CRUD operations will come from the base class. You only need to override `getPageByFilter` method with a custom filter function.
And implement a `getOneByKey` custom persistence method that doesn't exist in the base class.

```java
import org.pipservices3.commons.data.DataPage;
import org.pipservices3.commons.data.FilterParams;
import org.pipservices3.commons.data.PagingParams;

import java.util.Objects;
import java.util.function.Predicate;

public class MyMemoryPersistence extends IdentifiableMemoryPersistence<MyObject, String> {
    protected MyMemoryPersistence() {
        super(MyObject.class);
    }

    private Predicate<MyObject> composeFilter(FilterParams filter) {
        filter = filter != null ? filter : new FilterParams();
        var key = filter.getAsNullableString("key");

        return (item) -> {
            return key == null || Objects.equals(item.getKey(), key);
        };
    }

    public DataPage<MyObject> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging) {
        return super.getPageByFilter(correlationId, composeFilter(filter), paging, null);
    }

    public MyObject getOneByKey(String correlationId, String key) {
        synchronized(this._lock) {
          var item = this._items.stream().filter((el) -> el.getKey().equals(key)).findAny();

          if (item.isPresent())
              this._logger.trace(correlationId, "Found object by key=%s", key);
          else
              this._logger.trace(correlationId, "Cannot find by key=%s", key);
          
          return item.orElse(null);
        }
    }
}
```

It is easy to create file persistence by adding a persister object to the implemented in-memory persistence component.

```java
package org.pipservices3.data.persistence;

import org.pipservices3.commons.config.ConfigParams;
import org.pipservices3.commons.errors.ConfigException;

public class MyFilePersistence extends MyMemoryPersistence {
    protected JsonFilePersister<MyObject> _persister;

    public MyFilePersistence() {

    }

    public MyFilePersistence(String path) {
        if (path != null) {
            this._persister = new JsonFilePersister<MyObject>(MyObject.class, path);
            this._loader = this._persister;
            this._saver = this._persister;
        }
    }

    @Override
    public void configure(ConfigParams config) throws ConfigException {
        super.configure(config);
        this._persister.configure(config);
    }
}

```

Configuration for your microservice that includes memory and file persistence may look the following way.

```yaml
...
{{#if MEMORY_ENABLED}}
- descriptor: "myservice:persistence:memory:default:1.0"
{{/if}}

{{#if FILE_ENABLED}}
- descriptor: "myservice:persistence:file:default:1.0"
  path: {{FILE_PATH}}{{#unless FILE_PATH}}"../data/data.json"{{/unless}}
{{/if}}
...
```

## Develop

For development you shall install the following prerequisites:
* Java SE Development Kit 11+
* Eclipse Java Photon or another IDE of your choice
* Docker
* Apache Maven

Build the project:
```bash
mvn install
```

Run automated tests:
```bash
mvn test
```

Generate API documentation:
```bash
./docgen.ps1
```

Before committing changes run dockerized build and test as:
```bash
./build.ps1
./test.ps1
./clear.ps1
```

## Contacts

The initial implementation is done by **Sergey Seroukhov**. Pip.Services team is looking for volunteers to 
take ownership over Java implementation in the project.
