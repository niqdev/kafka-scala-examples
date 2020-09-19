# kafka-scala-examples

[![Build Status][travis-image]][travis-url]
[![Scala Steward badge][scala-steward-image]][scala-steward-url]

[travis-image]: https://travis-ci.org/niqdev/kafka-scala-examples.svg?branch=master
[travis-url]: https://travis-ci.org/niqdev/kafka-scala-examples
[scala-steward-image]: https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=
[scala-steward-url]: https://scala-steward.org

Examples in Scala of
* [Avro](#avro)
* [Kafka](#kafka)
* [Schema Registry](#schema-registry)
* [Kafka Streams](#kafka-streams)
    - with [cats](https://github.com/niqdev/kafka-scala-examples#demo-3)
    - with [ZIO](https://github.com/niqdev/kafka-scala-examples#demo-4), see also [zio-kafka-streams](https://github.com/niqdev/zio-kafka-streams)
* [KSQL](#ksql)
* [Kafka Connect](#kafka-connect)

Local environment

```bash
# start locally
# - zookeeper
# - kafka
# - kafka-rest
# - kafka-ui
# - schema-registry
# - schema-registry-ui
# - ksql-server
# - ksql-cli
# - kafka-connect
# - kafka-connect-ui
docker-compose up

# (mac|linux) view kafka ui
[open|xdg-open] http://localhost:8000

# (mac|linux) view schema-registry ui
[open|xdg-open] http://localhost:8001

# (mac|linux) view kafka-connect ui
[open|xdg-open] http://localhost:8002

# cleanup
docker-compose down -v
```

If containers are crashing, make sure you have enough resources

```bash
# verify memory and cpu usage
docker ps -q | xargs docker stats --no-stream

# verify status
docker inspect <CONTAINER_NAME> | jq '.[].State'
```

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
# access kafka
docker exec -it local-kafka bash

# create topic
# convention <MESSAGE_TYPE>.<DATASET_NAME>.<DATA_NAME>
# example [example.no-schema.original|example.no-schema.cakesolutions]
kafka-topics --zookeeper zookeeper:2181 \
  --create --if-not-exists --replication-factor 1 --partitions 1 --topic <TOPIC_NAME>

# delete topic
kafka-topics --zookeeper zookeeper:2181 \
  --delete --topic <TOPIC_NAME>

# view topic
kafka-topics --zookeeper zookeeper:2181 --list 
kafka-topics --zookeeper zookeeper:2181 --describe --topic <TOPIC_NAME>

# view topic offset
kafka-run-class kafka.tools.GetOffsetShell \
  --broker-list kafka:9092 \
  --time -1 \
  --topic <TOPIC_NAME>

# list consumer groups
kafka-consumer-groups --bootstrap-server kafka:9092 --list

# view consumer group offset
kafka-consumer-groups \
  --bootstrap-server kafka:9092 \
  --group <GROUP_NAME> \
  --describe

# reset consumer group offset
kafka-consumer-groups \
  --bootstrap-server kafka:9092 \
  --group <GROUP_NAME> \
  --topic <TOPIC_NAME> \
  --reset-offsets \
  --to-earliest \
  --execute

# console producer
kafka-console-producer --broker-list kafka:9092 --topic <TOPIC_NAME>
kafkacat -P -b 0 -t <TOPIC_NAME>

# console consumer
kafka-console-consumer --bootstrap-server kafka:9092 --topic <TOPIC_NAME> --from-beginning
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
docker exec -it local-kafka bash

# (optional) create topic
kafka-topics --zookeeper zookeeper:2181 \
  --create --if-not-exists --replication-factor 1 --partitions 1 --topic example.with-schema.payment

# console producer (binary)
kafka-console-producer --broker-list kafka:9092 --topic example.with-schema.payment

# console consumer (binary)
kafka-console-consumer --bootstrap-server kafka:9092 --topic example.with-schema.payment

# access schema-registry
docker exec -it local-schema-registry bash

# avro console producer
# example "MyKey",{"id":"MyId","amount":10}
kafka-avro-console-producer --broker-list kafka:29092 \
  --topic example.with-schema.payment \
  --property schema.registry.url=http://schema-registry:8081 \
  --property parse.key=true \
  --property key.separator=, \
  --property key.schema='{"type":"string"}' \
  --property value.schema='{"namespace":"io.confluent.examples.clients.basicavro","type":"record","name":"Payment","fields":[{"name":"id","type":"string"},{"name":"amount","type":"double"}]}'

# avro console consumer
kafka-avro-console-consumer --bootstrap-server kafka:29092 \
  --topic example.with-schema.payment \
  --property schema.registry.url=http://schema-registry:8081 \
  --property schema.id.separator=: \
  --property print.key=true \
  --property print.schema.ids=true \
  --property key.separator=, \
  --from-beginning

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

**Readings**

* [Serializing data efficiently with Apache Avro and dealing with a Schema Registry](https://www.sderosiaux.com/articles/2017/03/02/serializing-data-efficiently-with-apache-avro-and-dealing-with-a-schema-registry)
* [Kafka, Avro Serialization and the Schema Registry](http://cloudurable.com/blog/kafka-avro-schema-registry/index.html)
* [Kafka, Streams and Avro serialization](https://msayag.github.io/Kafka)
* [Avro and the Schema Registry](https://aseigneurin.github.io/2018/08/02/kafka-tutorial-4-avro-and-schema-registry.html)
* [Producing and Consuming Avro Messages over Kafka in Scala](https://medium.com/@lukasgrasse/producing-and-consuming-avro-messages-over-kafka-in-scala-edc26d69298c)

**Alternatives**

* [schema-repo](https://github.com/schema-repo/schema-repo)
* Hortonworks [Registry](https://registry-project.readthedocs.io/en/latest)

> TODO

* generic + schema evolution
* ovotech
* multi-schema
* formulation

## kafka-streams

**Description**

[Kafka Streams](https://kafka.apache.org/documentation/streams) apis examples

**Demo-1**

* `ToUpperCaseApp` [[source](streams/src/main/scala/com/kafka/demo/streams/ToUpperCaseApp.scala)|[test](streams/src/test/scala/com/kafka/demo/streams/ToUpperCaseSpec.scala)]

```bash
# access kafka
docker exec -it local-kafka bash

# create topic
# example [example.to-upper-case-app.input|example.to-upper-case-app.output]
kafka-topics --zookeeper zookeeper:2181 \
  --create --if-not-exists --replication-factor 1 --partitions 1 --topic <TOPIC_NAME>

# ToUpperCaseApp example (input topic required)
sbt "streams/runMain com.kafka.demo.streams.ToUpperCaseApp"

# produce
kafka-console-producer --broker-list kafka:9092 \
  --topic example.to-upper-case-app.input

# consume
kafka-console-consumer --bootstrap-server kafka:9092 \
  --topic example.to-upper-case-app.output

# test
sbt clean streams/test
```

**Demo-2**

Tested with [embedded-kafka](https://github.com/embeddedkafka/embedded-kafka) and [embedded-kafka-schema-registry](https://github.com/embeddedkafka/embedded-kafka-schema-registry)

* `JsonToAvroApp` [[source](https://github.com/niqdev/kafka-scala-examples/blob/master/streams-json-avro/src/main/scala/com/kafka/demo/JsonToAvroApp.scala)|[test](https://github.com/niqdev/kafka-scala-examples/blob/master/streams-json-avro/src/test/scala/com/kafka/demo/JsonToAvroSpec.scala)]

```bash
# access kafka
docker exec -it local-kafka bash

# create topic
# example [json.streams-json-to-avro-app.input|avro.streams-json-to-avro-app.output]
kafka-topics --zookeeper zookeeper:2181 \
  --create --if-not-exists --replication-factor 1 --partitions 1 --topic <TOPIC_NAME>

# produce (default StringSerializer)
kafka-console-producer \
  --broker-list kafka:9092 \
  --property "parse.key=true" \
  --property "key.separator=:" \
  --property "key.serializer=org.apache.kafka.common.serialization.ByteArraySerializer" \
  --property "value.serializer=org.apache.kafka.common.serialization.ByteArraySerializer" \
  --topic <TOPIC_NAME>

# consume (default StringDeserializer)
kafka-console-consumer \
  --bootstrap-server kafka:9092 \
  --from-beginning \
  --property "print.key=true" \
  --property "key.deserializer=org.apache.kafka.common.serialization.ByteArrayDeserializer" \
  --property "value.deserializer=org.apache.kafka.common.serialization.ByteArrayDeserializer" \
  --topic <TOPIC_NAME>

# access schema-registry
docker exec -it local-schema-registry bash

# consume avro
kafka-avro-console-consumer --bootstrap-server kafka:29092 \
  --property schema.registry.url=http://schema-registry:8081 \
  --property schema.id.separator=: \
  --property print.key=true \
  --property print.schema.ids=true \
  --property key.separator=, \
  --from-beginning \
  --topic <TOPIC_NAME>

# JsonToAvroApp example (input topic required)
sbt "streams-json-avro/runMain com.kafka.demo.JsonToAvroApp"

# test
sbt clean streams-json-avro/test
```

Example
```
# json
mykey:{"valueInt":42,"valueString":"foo"}

# log
[json.streams-json-to-avro-app.input]: mykey, JsonModel(42,foo)
[avro.streams-json-to-avro-app.output]: KeyAvroModel(mykey), ValueAvroModel(42,FOO)
```

#### Demo-3

> TODO

* `CatsKafkaStreamsApp` [[source](https://github.com/niqdev/kafka-scala-examples/blob/master/cats-kafka-streams/src/main/scala/com/kafka/demo/CatsKafkaStreamsApp.scala)]

```bash
# run app
sbt -jvm-debug 5005 "cats-kafka-streams/runMain com.kafka.demo.CatsKafkaStreamsApp"
```

#### Demo-4

> TODO

* `ZioKafkaStreamsApp` [[source](https://github.com/niqdev/kafka-scala-examples/blob/master/zio-kafka-streams/src/main/scala/com/kafka/demo/ZioKafkaStreamsApp.scala)]

```bash
# run app
sbt -jvm-debug 5005 "zio-kafka-streams/runMain com.kafka.demo.ZioKafkaStreamsApp"
```

**Readings**

* [Introducing Kafka Streams: Stream Processing Made Simple](https://www.confluent.io/blog/introducing-kafka-streams-stream-processing-made-simple)
* [Unifying Stream Processing and Interactive Queries in Apache Kafka](https://www.confluent.io/blog/unifying-stream-processing-and-interactive-queries-in-apache-kafka)
* [Of Streams and Tables in Kafka and Stream Processing](https://www.michael-noll.com/blog/2018/04/05/of-stream-and-tables-in-kafka-and-stream-processing-part1)
* [Functional Programming with Kafka Streams and Scala](https://itnext.io/a-cqrs-approach-with-kafka-streams-and-scala-49bfa78e4295)

## ksql

**Description**

* [KSQL](https://docs.confluent.io/current/ksql/docs/index.html)
* [ksqlDB](https://docs.ksqldb.io/en/latest)
* [Udemy Course](https://www.udemy.com/kafka-ksql)

Setup Kafka
```bash
# access kafka
docker exec -it local-kafka bash

# create topic
kafka-topics --zookeeper zookeeper:2181 \
  --create --if-not-exists --replication-factor 1 --partitions 1 --topic USER_PROFILE

# produce sample data
kafka-console-producer --broker-list kafka:9092 --topic USER_PROFILE << EOF
{"userid": 1000, "firstname": "Alison", "lastname": "Smith", "countrycode": "GB", "rating": 4.7}
EOF

# consume
kafka-console-consumer --bootstrap-server kafka:9092 --topic USER_PROFILE --from-beginning
```

Access KSQL CLI

* using the server
    ```bash
    # access ksql-server
    docker exec -it local-ksql-server bash
    
    # start ksql cli
    ksql http://ksql-server:8088
    ```

* using a local instance
    ```bash
    # connect to local cli
    docker exec -it local-ksql-cli ksql http://ksql-server:8088
    ```

* using a temporary instance
    ```bash
    # connect to remote server
    docker run --rm \
      --network=kafka-scala-examples_local_kafka_network \
      -it confluentinc/cp-ksql-cli http://ksql-server:8088
    ```

Execute SQL statements
```bash
# create stream
CREATE STREAM user_profile (\
  userid INT, \
  firstname VARCHAR, \
  lastname VARCHAR, \
  countrycode VARCHAR, \
  rating DOUBLE \
  ) WITH (KAFKA_TOPIC = 'USER_PROFILE', VALUE_FORMAT = 'JSON');

# verify stream
list streams;
describe user_profile;

# query stream
SELECT userid, firstname, lastname, countrycode, rating FROM user_profile EMIT CHANGES;
```

Expect the consumer and the query to show the generated data
```bash
# generate data
docker run --rm \
  -v $(pwd)/local/ksql:/datagen \
  --network=kafka-scala-examples_local_kafka_network \
  -it confluentinc/ksql-examples ksql-datagen \
  bootstrap-server=kafka:29092 \
  schemaRegistryUrl=http://schema-registry:8081 \
  schema=datagen/user_profile.avro \
  format=json \
  topic=USER_PROFILE \
  key=userid \
  maxInterval=5000 \
  iterations=100
```

## kafka-connect

* [Kafka Connect](https://docs.confluent.io/current/connect/index.html)
* Confluent's Kafka Connect [API](https://docs.confluent.io/current/connect/references/restapi.html) and [connectors](https://docs.confluent.io/current/connect/managing/connectors.html)
* [Udemy Course](https://www.udemy.com/course/kafka-connect)

Setup PostgreSQL locally
```bash
# create shared network
docker-compose up

# start postgres
docker-compose -f docker-compose.postgres.yml up

# (mac|linux) view postgres ui
# [schema=public|database=postgres|username=postgres|password=postgres]
[open|xdg-open] http://localhost:8080
```

Setup connectors

* `kafka-connect-spooldir` [[confluent](https://docs.confluent.io/current/connect/kafka-connect-spooldir/index.html) | [official](https://jcustenborder.github.io/kafka-connect-documentation/projects/kafka-connect-spooldir/index.html)]
* `kafka-connect-jdbc` [[confluent](https://docs.confluent.io/current/connect/kafka-connect-jdbc/index.html)]

```bash
# list connector
http -v :8083/connectors

# init data to generate schema
cp local/connect/data/resources-0.txt.orig local/connect/data/resources-0.txt

# setup spooldir source connector
http -v --json POST :8083/connectors < local/connect/config/source-spooldir-connector.json

# ingest data
echo "{\"accountId\":\"123\",\"resourceType\":\"XXX\",\"value\":\"X1\"}" > local/connect/data/resources-1.txt

# setup jdbc sink connector
# topic = SCHEMA.DATABASE = "public.postgres"
http -v --json POST :8083/connectors < local/connect/config/sink-jdbc-connector.json

# verify data
docker exec -it local-postgres bash -c "psql -U postgres postgres"
select * from public.postgres;

# cleanup
docker-compose -f docker-compose.postgres.yml down -v
```

## extra

**Further readings**

* *Old [presentation](https://gitpitch.com/niqdev/kafka-scala-examples)*
* [What is the actual role of Zookeeper in Kafka?](https://www.quora.com/What-is-the-actual-role-of-Zookeeper-in-Kafka-What-benefits-will-I-miss-out-on-if-I-don%E2%80%99t-use-Zookeeper-and-Kafka-together/answer/Gwen-Shapira)
* [How to use Apache Kafka to transform a batch pipeline into a real-time one](https://medium.com/@stephane.maarek/how-to-use-apache-kafka-to-transform-a-batch-pipeline-into-a-real-time-one-831b48a6ad85)
* [Kafka Partitioning](https://simplydistributed.wordpress.com/2016/12/13/kafka-partitioning)
* [Should you put several event types in the same Kafka topic?](http://martin.kleppmann.com/2018/01/18/event-types-in-kafka-topic.html)
* [How to choose the number of topics/partitions in a Kafka cluster?](https://www.confluent.io/blog/how-choose-number-topics-partitions-kafka-cluster)
* [Docker Tips and Tricks with KSQL and Kafka](https://rmoff.net/2018/12/15/docker-tips-and-tricks-with-ksql-and-kafka)
* [Introduction to Topic Log Compaction in Apache Kafka](https://medium.com/swlh/introduction-to-topic-log-compaction-in-apache-kafka-3e4d4afd2262)

**Tools**

* [kafkacat](https://github.com/edenhill/kafkacat)
* [Kafka-Utils](https://github.com/Yelp/kafka-utils)
* [HTTPie](https://httpie.org)
* [jq](https://stedolan.github.io/jq)
