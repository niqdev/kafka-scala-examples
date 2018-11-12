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

An Avro schema defines the data structure in a JSON

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
