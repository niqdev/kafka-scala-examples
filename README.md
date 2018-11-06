# kafka-demo

[![Build Status][travis-image]][travis-url]

[travis-image]: https://travis-ci.org/niqdev/kafka-demo.svg?branch=master
[travis-url]: https://travis-ci.org/niqdev/kafka-demo

> Work In Progress

Examples of Avro, Kafka, Schema Registry, Kafka Stream, KSQL in Scala

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

## kafka

**Description**

[Kafka](https://kafka.apache.org/documentation) apis example of
* `KafkaProducer` [[docs](https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html)|[source](kafka/src/main/scala/com/kafka/demo/original/Producer.scala)]
and `KafkaConsumer` [[docs](https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html)|[source](kafka/src/main/scala/com/kafka/demo/original/Consumer.scala)]
clients
* [CakeSolutions](https://github.com/cakesolutions/scala-kafka-client)
`KafkaProducer` [[source](kafka/src/main/scala/com/kafka/demo/cakesolutions/Producer.scala)|[test](kafka/src/test/scala/com/kafka/demo/cakesolutions/KafkaSpec.scala)]
and `KafkaConsumer` [[source](kafka/src/main/scala/com/kafka/demo/cakesolutions/Consumer.scala)|[test](kafka/src/test/scala/com/kafka/demo/cakesolutions/KafkaSpec.scala)]
clients

```bash
# start zookeeper + kafka + kafka-ui
docker-compose up

# (mac|linux) view kafka ui
[open|xdg-open] http://localhost:8000

# access kafka
docker exec -it my-local-kafka bash

# create topic
# example [topic-no-schema-original|topic-no-schema-cakesolutions]
kafka-topics.sh --zookeeper zookeeper:2181 \
  --create --if-not-exists --replication-factor 1 --partitions 1 --topic <TOPIC_NAME>

# delete topic
kafka-topics.sh --zookeeper zookeeper:2181 \
  --delete --topic <TOPIC_NAME>

# view topic
kafka-topics.sh --zookeeper zookeeper:2181 --list 
kafka-topics.sh --zookeeper zookeeper:2181 --describe --topic <TOPIC_NAME>

# console producer
kafka-console-producer.sh --broker-list kafka:9092 --topic <TOPIC_NAME>
kafkacat -P -b 0 -t <TOPIC_NAME>

# console consumer
kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic <TOPIC_NAME> --from-beginning
kafkacat -C -b 0 -t <TOPIC_NAME>

# producer example
sbt "kafka/runMain com.kafka.demo.original.Producer"
sbt "kafka/runMain com.kafka.demo.cakesolutions.Producer"

# consumer example
sbt "kafka/runMain com.kafka.demo.original.Consumer"
sbt "kafka/runMain com.kafka.demo.cakesolutions.Consumer"

# test
sbt clean kafka/test
sbt "test:testOnly *KafkaSpec"
```

## schema-registry

**Description**

> TODO

## stream

**Description**

> TODO

## extra

**Further readings**

* [How to use Apache Kafka to transform a batch pipeline into a real-time one](https://medium.com/@stephane.maarek/how-to-use-apache-kafka-to-transform-a-batch-pipeline-into-a-real-time-one-831b48a6ad85)
* [Should you put several event types in the same Kafka topic?](http://martin.kleppmann.com/2018/01/18/event-types-in-kafka-topic.html)
