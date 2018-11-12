## Data Serialization and Evolution

---

## Avro

+++

### Alternatives to encode the data into bytes

* @color[orange](Java serialization) makes consuming the data in other languages inconvenient
* @color[orange](XML) and @color[orange](JSON) drawbacks
  - add or remove fields may cause data corruption
  - field names and type information are verbose and reduce performances
* alternatives are @color[green](Avro), @color[green](Thrift) and @color[green](Protocol Buffers)
* Avro is recommended by Confluent

+++

An Avro schema defines the data structure in JSON

User.avsc
```
{
  "namespace": "com.kafka.demo",
  "type": "record",
  "name": "User",
  "fields": [
    {
      "name": "name",
      "type": "string"
    }
  ]
}
```

+++

* Avro can be used to serialize a Java object (POJO) into bytes and viceversa
* it requires a schema during both data serialization and deserialization
* provides schema evolution globally or for subjects
  - @color[green](Backward Compatibility) data encoded with an older schema can be read with a newer schema
  - @color[orange](Forward Compatibility)
  - @color[orange](Full Compatibility)

---

## Schema Registry

+++

* It provides a RESTful interface for storing and retrieving Avro schemas
* It assigns globally unique ID to each registered schema
* It uses Kafka as its underlying storage mechanism
* It is designed to be distributed, with single-master architecture

+++

### Demo

* [ansible](https://github.com/niqdev/devops/tree/master/ansible/data/roles/schema-registry)
* [docker](https://github.com/niqdev/kafka-demo#schema-registry)

---
