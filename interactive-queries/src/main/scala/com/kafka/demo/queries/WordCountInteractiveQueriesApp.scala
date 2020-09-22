package com.kafka.demo.queries

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.Logger
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{ KafkaStreams, StreamsConfig, Topology }

// https://docs.confluent.io/current/streams/developer-guide/interactive-queries.html
// https://github.com/confluentinc/kafka-streams-examples/blob/5.5.1-post/src/main/java/io/confluent/examples/streams/interactivequeries/WordCountInteractiveQueriesExample.java
object WordCountInteractiveQueriesApp {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val APP_NAME                = "word-count-interactive-queries-app"
  private[this] val IN_TOPIC                = s"example.$APP_NAME.input"
  private[this] val OUT_TOPIC               = s"example.$APP_NAME.output"
  private[this] val TIMEOUT_SEC             = 60

  private[this] def properties: Properties = {
    val props = new Properties()
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_VALUE)
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_NAME)
    props
  }

  private[this] def topology: Topology = {
    val builder = new StreamsBuilder()
    // TODO
    builder.build()
  }

  // sbt "interactive-queries/runMain com.kafka.demo.queries.WordCountInteractiveQueriesApp"
  def main(args: Array[String]): Unit = {
    val kafkaStreams = new KafkaStreams(topology, properties)
    kafkaStreams.start()
    logger.info(s"Start streaming app: $APP_NAME")

    TimeUnit.SECONDS.sleep(TIMEOUT_SEC)
    kafkaStreams.close()
    logger.info(s"Stopping streaming app: $APP_NAME")
  }
}
