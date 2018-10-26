# kafka-demo

[![Build Status][travis-image]][travis-url]

[travis-image]: https://travis-ci.org/niqdev/kafka-demo.svg?branch=master
[travis-url]: https://travis-ci.org/niqdev/kafka-demo

> Work In Progress

Examples of Avro, Schema Registry, Kafka Stream, KSQL in Scala

**Further readings**

* [How to use Apache Kafka to transform a batch pipeline into a real-time one](https://medium.com/@stephane.maarek/how-to-use-apache-kafka-to-transform-a-batch-pipeline-into-a-real-time-one-831b48a6ad85)
* [Should you put several event types in the same Kafka topic?](http://martin.kleppmann.com/2018/01/18/event-types-in-kafka-topic.html)

## avro

**Description**

[Avro](https://avro.apache.org/docs/current/gettingstartedjava.html) serialization and deserialization examples of
* code generation with [sbt-avro](https://github.com/sbt/sbt-avro) [[source](avro/src/main/scala/com/kafka/demo/original/AvroCodeGeneration.scala)|[test](avro/src/test/scala/com/kafka/demo/original/AvroCodeGenerationSpec.scala)]
* `GenericRecord` [[source](avro/src/main/scala/com/kafka/demo/original/AvroGenericRecord.scala)|[test](avro/src/test/scala/com/kafka/demo/original/AvroGenericRecordSpec.scala)]
* [avro4s](https://github.com/sksamuel/avro4s) [[source](avro/src/main/scala/com/kafka/demo/avro4s/Avro4sExample.scala)|[test](avro/src/test/scala/com/kafka/demo/avro4s/Avro4sExampleSpec.scala)]
* Java/Scala libraries compatibility [[test](avro/src/test/scala/com/kafka/demo/LibraryCompatibilitySpec.scala)]

```bash
# console
sbt avro/console

# generate avro classes
# avro/target/scala-2.12/classes/com/kafka/demo/User.class
sbt clean avro/compile

# tests
sbt clean avro/test
```

**Readings**

* [Data Serialization and Evolution](https://docs.confluent.io/current/avro.html)
* [Three Reasons Why Apache Avro Data Serialization is a Good Choice](https://blog.cloudera.com/blog/2011/05/three-reasons-why-apache-avro-data-serialization-is-a-good-choice-for-openrtb)
* [Schema evolution in Avro, Protocol Buffers and Thrift](http://martin.kleppmann.com/2012/12/05/schema-evolution-in-avro-protocol-buffers-thrift.html)
* [Avro Introduction for Big Data and Data Streaming Architectures](https://web.archive.org/web/20180321143507/http://cloudurable.com/blog/avro/index.html)
* [Stream Avro Records into Kafka using Avro4s and Akka Streams Kafka](https://abhsrivastava.github.io/2017/10/02/Stream-Avro-Records-into-Kafka)
* [Kafka, Spark and Avro](https://aseigneurin.github.io/2016/03/02/kafka-spark-avro-kafka-101.html)

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
