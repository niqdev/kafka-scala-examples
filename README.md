# kafka-demo

[![Build Status][travis-image]][travis-url]

[travis-image]: https://travis-ci.org/niqdev/kafka-demo.svg?branch=master
[travis-url]: https://travis-ci.org/niqdev/kafka-demo

> Work In Progress

Examples of Avro, Kafka, Schema Registry, Kafka Stream, KSQL in Scala

## avro

**Description**

[Avro](https://avro.apache.org/docs/current/gettingstartedjava.html) serialization and deserialization examples of
* `SpecificRecord` code generation with [sbt-avro](https://github.com/sbt/sbt-avro) [[source](avro/src/main/scala/com/kafka/demo/original/AvroCodeGeneration.scala)|[test](avro/src/test/scala/com/kafka/demo/original/AvroCodeGenerationSpec.scala)]
* `GenericRecord` [[source](avro/src/main/scala/com/kafka/demo/original/AvroGenericRecord.scala)|[test](avro/src/test/scala/com/kafka/demo/original/AvroGenericRecordSpec.scala)]
* [avro4s](https://github.com/sksamuel/avro4s) [[source](avro/src/main/scala/com/kafka/demo/avro4s/Avro4sExample.scala)|[test](avro/src/test/scala/com/kafka/demo/avro4s/Avro4sExampleSpec.scala)]
* Java/Scala libraries compatibility [[test](avro/src/test/scala/com/kafka/demo/LibraryCompatibilitySpec.scala)]

**Demo**

```bash
# console
sbt avro/console

# generate avro classes
# avro/target/scala-2.12/classes/com/kafka/demo/User.class
sbt clean avro/compile

# test
sbt clean avro/test
```

**Readings**

* [Data Serialization and Evolution](https://docs.confluent.io/current/avro.html)
* [Three Reasons Why Apache Avro Data Serialization is a Good Choice](https://blog.cloudera.com/blog/2011/05/three-reasons-why-apache-avro-data-serialization-is-a-good-choice-for-openrtb)
* [Schema evolution in Avro, Protocol Buffers and Thrift](http://martin.kleppmann.com/2012/12/05/schema-evolution-in-avro-protocol-buffers-thrift.html)
* [Avro Introduction for Big Data and Data Streaming Architectures](http://cloudurable.com/blog/avro/index.html)
* [Stream Avro Records into Kafka using Avro4s and Akka Streams Kafka](https://abhsrivastava.github.io/2017/10/02/Stream-Avro-Records-into-Kafka)
* [Kafka, Spark and Avro](https://aseigneurin.github.io/2016/03/02/kafka-spark-avro-kafka-101.html)

## kafka

**Description**

[Kafka](https://kafka.apache.org/documentation) apis example of
* `KafkaProducer` [[doc](https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html)|[source](kafka/src/main/scala/com/kafka/demo/original/Producer.scala)]
and `KafkaConsumer` [[doc](https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html)|[source](kafka/src/main/scala/com/kafka/demo/original/Consumer.scala)]
clients
* [CakeSolutions](https://github.com/cakesolutions/scala-kafka-client)
`KafkaProducer` [[source](kafka/src/main/scala/com/kafka/demo/cakesolutions/Producer.scala)|[test](kafka/src/test/scala/com/kafka/demo/cakesolutions/KafkaSpec.scala)]
and `KafkaConsumer` [[source](kafka/src/main/scala/com/kafka/demo/cakesolutions/Consumer.scala)|[test](kafka/src/test/scala/com/kafka/demo/cakesolutions/KafkaSpec.scala)]
clients

**Demo**

```bash
# start zookeeper + kafka + kafka-rest + kafka-ui
docker-compose up

# (mac|linux) view kafka ui
[open|xdg-open] http://localhost:8000

# access kafka
docker exec -it my-local-kafka bash

# create topic
# convention <MESSAGE_TYPE>.<DATASET_NAME>.<DATA_NAME>
# example [example.no-schema.original|example.no-schema.cakesolutions]
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

**Readings**

* DevOps [Kafka](https://niqdev.github.io/devops/kafka)
* [Kafka topic naming conventions](https://medium.com/@criccomini/how-to-paint-a-bike-shed-kafka-topic-naming-conventions-1b7259790073)

## schema-registry

**Description**

* Confluent's Schema Registry [API](https://docs.confluent.io/current/schema-registry/docs/api.html)
and [examples](https://docs.confluent.io/current/schema-registry/docs/using.html#common-sr-usage-examples)
* Console [examples](https://docs.confluent.io/current/schema-registry/docs/serializer-formatter.html#formatter)

```bash
# register schema
# convention <TOPIC_NAME>-key or <TOPIC_NAME>-value
http -v POST :8081/subjects/example.with-schema.simple-value/versions \
  Accept:application/vnd.schemaregistry.v1+json \
  schema='{"type":"string"}'

# import schema from file
http -v POST :8081/subjects/example.with-schema.user-value/versions \
  Accept:application/vnd.schemaregistry.v1+json \
  schema=@avro/src/main/avro/user.avsc

# export schema to file
http :8081/subjects/example.with-schema.user-value/versions/latest \
  | jq -r '.schema|fromjson' \
  | tee avro/src/main/avro/user-latest.avsc

# list subjects
http -v :8081/subjects

# list subject's versions
http -v :8081/subjects/example.with-schema.simple-value/versions

# fetch by version
http -v :8081/subjects/example.with-schema.simple-value/versions/1

# fetch by id
http -v :8081/schemas/ids/1

# test compatibility
http -v POST :8081/compatibility/subjects/example.with-schema.simple-value/versions/latest \
  Accept:application/vnd.schemaregistry.v1+json \
  schema='{"type":"string"}'

# delete version
http -v DELETE :8081/subjects/example.with-schema.simple-value/versions/1

# delete latest version
http -v DELETE :8081/subjects/example.with-schema.simple-value/versions/latest

# delete subject
http -v DELETE :8081/subjects/example.with-schema.simple-value

# stringify
jq tostring avro/src/main/avro/user.avsc
```

**Demo**

* [`BaseKafkaSchemaRegistrySpec`](schema-registry/src/test/scala/com/kafka/demo/BaseKafkaSchemaRegistrySpec.scala) to test Kafka with SchemaRegistry

Setup

```bash
# start zookeeper + kafka + kafka-rest + kafka-ui + schema-registry + schema-registry-ui
docker-compose up

# (mac|linux) view kafka ui
[open|xdg-open] http://localhost:8000

# (mac|linux) view schema-registry ui
[open|xdg-open] http://localhost:8001
```

* `SpecificRecord` with [sbt-avrohugger](https://github.com/julianpeeters/sbt-avrohugger)
[[Producer](schema-registry/src/main/scala/com/kafka/demo/specific/Producer.scala)|[Consumer](schema-registry/src/main/scala/com/kafka/demo/specific/Consumer.scala)|[test](schema-registry/src/test/scala/com/kafka/demo/KafkaSchemaRegistrySpecificSpec.scala)]

```bash
# generate SpecificRecord classes under "schema-registry/target/scala-2.12/src_managed/main/compiled_avro"
sbt clean schema-registry/avroScalaGenerateSpecific

# (optional) create schema
http -v POST :8081/subjects/example.with-schema.payment-key/versions \
  Accept:application/vnd.schemaregistry.v1+json \
  schema='{"type":"string"}'
http -v POST :8081/subjects/example.with-schema.payment-value/versions \
  Accept:application/vnd.schemaregistry.v1+json \
  schema=@schema-registry/src/main/avro/Payment.avsc

# access kafka
docker exec -it my-local-kafka bash

# (optional) create topic
kafka-topics.sh --zookeeper zookeeper:2181 \
  --create --if-not-exists --replication-factor 1 --partitions 1 --topic example.with-schema.payment

# console producer (binary)
kafkacat -P -b 0 -t example.with-schema.payment

# console consumer (binary)
kafkacat -C -b 0 -t example.with-schema.payment

# access schema-registry
docker exec -it my-local-schema-registry bash

# avro console producer
# example "MyKey",{"id":"MyId","amount":10}
kafka-avro-console-producer --broker-list kafka:9092 \
  --topic example.with-schema.payment \
  --property schema.registry.url=http://schema-registry:8081 \
  --property parse.key=true \
  --property key.separator=, \
  --property key.schema='{"type":"string"}' \
  --property value.schema='{"namespace":"io.confluent.examples.clients.basicavro","type":"record","name":"Payment","fields":[{"name":"id","type":"string"},{"name":"amount","type":"double"}]}'

# avro console consumer
kafka-avro-console-consumer --bootstrap-server kafka:9092 \
  --topic example.with-schema.payment \
  --property schema.registry.url=http://schema-registry:8081 \
  --property schema.id.separator=: \
  --property print.key=true \
  --property print.schema.ids=true \
  --property key.separator=,

# producer example
sbt "schema-registry/runMain com.kafka.demo.specific.Producer"

# consumer example
sbt "schema-registry/runMain com.kafka.demo.specific.Consumer"

# tests
sbt "schema-registry/test:testOnly *KafkaSchemaRegistrySpecificSpec"
```

* `GenericRecord` with [CakeSolutions](https://github.com/cakesolutions/scala-kafka-client)
[[Producer](schema-registry/src/main/scala/com/kafka/demo/generic/Producer.scala)|[Consumer](schema-registry/src/main/scala/com/kafka/demo/generic/Consumer.scala)] and schema evolution [test](schema-registry/src/test/scala/com/kafka/demo/KafkaSchemaRegistryGenericSpec.scala)

```bash
# producer example
sbt "schema-registry/runMain com.kafka.demo.generic.Producer"

# consumer example
sbt "schema-registry/runMain com.kafka.demo.generic.Consumer"

# tests
sbt "schema-registry/test:testOnly *KafkaSchemaRegistryGenericSpec"
```

> TODO

* generic + schema evolution
* ovotech
* multi-schema
* formulation

**Readings**

* [Serializing data efficiently with Apache Avro and dealing with a Schema Registry](https://www.sderosiaux.com/articles/2017/03/02/serializing-data-efficiently-with-apache-avro-and-dealing-with-a-schema-registry)
* [Kafka, Avro Serialization and the Schema Registry](http://cloudurable.com/blog/kafka-avro-schema-registry/index.html)
* [Kafka, Streams and Avro serialization](https://msayag.github.io/Kafka)
* [Avro and the Schema Registry](https://aseigneurin.github.io/2018/08/02/kafka-tutorial-4-avro-and-schema-registry.html)
* [Producing and Consuming Avro Messages over Kafka in Scala](https://medium.com/@lukasgrasse/producing-and-consuming-avro-messages-over-kafka-in-scala-edc26d69298c)

**Alternatives**

* [schema-repo](https://github.com/schema-repo/schema-repo)
* Hortonworks [Registry](https://registry-project.readthedocs.io/en/latest)

**Tools**

* [HTTPie](https://httpie.org)
* [jq](https://stedolan.github.io/jq)

## streams

**Description**

> TODO

* [Kafka Streams](https://kafka.apache.org/documentation/streams) documentation

**Demo**

```bash
# toUpperCase example
sbt "streams/runMain com.kafka.demo.streams.ToUpperCaseApp"
```

**Readings**

* [Functional Programming with Kafka Streams and Scala](https://itnext.io/a-cqrs-approach-with-kafka-streams-and-scala-49bfa78e4295)

## extra

**Further readings**

* [How to use Apache Kafka to transform a batch pipeline into a real-time one](https://medium.com/@stephane.maarek/how-to-use-apache-kafka-to-transform-a-batch-pipeline-into-a-real-time-one-831b48a6ad85)
* [Should you put several event types in the same Kafka topic?](http://martin.kleppmann.com/2018/01/18/event-types-in-kafka-topic.html)
* [What is the actual role of Zookeeper in Kafka?](https://www.quora.com/What-is-the-actual-role-of-Zookeeper-in-Kafka-What-benefits-will-I-miss-out-on-if-I-don%E2%80%99t-use-Zookeeper-and-Kafka-together/answer/Gwen-Shapira)
* [How to choose the number of topics/partitions in a Kafka cluster?](https://www.confluent.io/blog/how-choose-number-topics-partitions-kafka-cluster)
* [Kafka Partitioning](https://simplydistributed.wordpress.com/2016/12/13/kafka-partitioning)
