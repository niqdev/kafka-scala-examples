# kafka-demo

[![Build Status][travis-image]][travis-url]

[travis-image]: https://travis-ci.org/niqdev/kafka-demo.svg?branch=master
[travis-url]: https://travis-ci.org/niqdev/kafka-demo

> Work In Progress

Examples of Avro, Schema Registry, Kafka Stream, KSQL in Scala

## avro

**Description**

[Avro](https://avro.apache.org/docs/current/gettingstartedjava.html) serialization and deserialization examples with
* code generation [[source](avro/src/main/scala/com/kafka/demo/original/AvroCodeGeneration.scala)|[test](avro/src/test/scala/com/kafka/demo/original/AvroCodeGenerationSpec.scala)]
* `GenericRecord` [[source](avro/src/main/scala/com/kafka/demo/original/AvroGenericRecord.scala)|[test](avro/src/test/scala/com/kafka/demo/original/AvroGenericRecordSpec.scala)]
* [avro4s](https://github.com/sksamuel/avro4s) [[source](avro/src/main/scala/com/kafka/demo/avro4s/Avro4sExample.scala)|[test](avro/src/test/scala/com/kafka/demo/avro4s/Avro4sExampleSpec.scala)]

```bash
# console
sbt avro/console

# generate avro classes
# avro/target/scala-2.12/classes/com/kafka/demo/User.class
sbt clean avro/compile

# tests
sbt clean avro/test
```

## schema-registry

**Description**

> TODO

```bash
sbt console
sbt clean test
sbt "test:testOnly *HelloSpec"

sbt "schema-registry/runMain com.kafka.demo.Main"
sbt "schema-registry/run com.kafka.demo.Main"

docker-compose up
```

## stream

**Description**

> TODO
