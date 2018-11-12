## Schema Registry demo

---

@ul[square-bullets](false)

- Avro
- Schema Registry

@ulend

---

## Avro

+++

#### Alternatives to encode the data into bytes

* Java serialization makes consuming the data in other languages inconvenient
* XML and JSON drawbacks
  - add or remove fields may cause data corruption
  - field names and type information are verbose and reduce performances
* alternatives are Avro, Thrift and Protocol Buffers
* Avro is recommended by Confluent

+++

An Avro schema defines the data structure in a JSON format

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
  - Backward Compatibility (default) means that data encoded with an older schema can be read with a newer schema
  - Forward Compatibility
  - Full Compatibility

---

## Schema Registry
