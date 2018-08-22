# kafka-stream-demo

## Local deployment

Docker

```bash
# build image
sbt docker:publishLocal

# start containers
docker-compose up

# access containers
docker exec -it demo-zookeeper bash
docker exec -it demo-kafka bash

# verify zookeeper (on zookeeper container)
echo ruok | nc localhost 2181

# create topic (on kafka container)
kafka-topics.sh --zookeeper zookeeper:2181 \
  --create --replication-factor 1 --partitions 1 --topic events

# update hosts (on host machine)
echo "127.0.0.1 kafka" | sudo tee -a /etc/hosts

# execute kafkacat commands on host machine
# https://github.com/edenhill/kafkacat

# list metadata
kafkacat -L -b kafka:9092

# produce
kafkacat -P -b kafka:9092 -t events

# consume
kafkacat -C -b kafka:9092 -t events
```

WordCountScalaExample
```bash
kafka-topics.sh --create --topic streams-plaintext-input --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1
kafka-topics.sh --create --topic streams-wordcount-output --zookeeper zookeeper:2181 --partitions 1 --replication-factor 1

kafka-console-producer.sh --broker-list localhost:9092 --topic streams-plaintext-input

kafka-console-consumer.sh --topic streams-wordcount-output --from-beginning \
  --bootstrap-server localhost:9092 \
  --property print.key=true \
  --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

```bash
docker-compose up

sbt app/run


```
