# kafka-demo

> TODO

## avro

```bash
sbt avro/console

# generate avro classes
# avro/target/scala-2.12/classes/com/kafka/demo/User.class
sbt clean avro/compile

# tests
sbt clean avro/test
```

# schema-registry

```bash
sbt console
sbt clean test
sbt "test:testOnly *HelloSpec"

sbt "schema-registry/runMain com.kafka.demo.Main"
sbt "schema-registry/run com.kafka.demo.Main"

docker-compose up
```
