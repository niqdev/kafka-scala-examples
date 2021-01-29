package com.kafka.demo.queries

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.Logger
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.scala.{ Serdes, StreamsBuilder }
import org.apache.kafka.streams.state.{ KeyValueIterator, QueryableStoreTypes, ReadOnlyKeyValueStore }
import org.apache.kafka.streams.{ KafkaStreams, StoreQueryParameters, StreamsConfig, Topology }

import scala.collection.JavaConverters._

// https://docs.confluent.io/current/streams/developer-guide/interactive-queries.html
// https://github.com/confluentinc/kafka-streams-examples/blob/5.5.1-post/src/main/java/io/confluent/examples/streams/interactivequeries/WordCountInteractiveQueriesExample.java
object WordCountInteractiveQueriesApp {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val APP_NAME                = "word-count-interactive-queries-app"
  private[this] val INPUT_TOPIC             = s"example.$APP_NAME.input"
  private[this] val OUTPUT_TOPIC            = s"example.$APP_NAME.output"
  private[this] val COUNT_STATE_STORE       = "word-count"
  private[this] val TIMEOUT_SEC             = 60

  private[this] def properties: Properties = {
    val props = new Properties()
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_VALUE)
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_NAME)
    props
  }

  private[this] def topology: Topology = {
    val builder = new StreamsBuilder()

    val textLines: KStream[String, String] =
      builder.stream(INPUT_TOPIC)(Consumed.`with`(Serdes.String, Serdes.String))
    val groupedByWord: KGroupedStream[String, String] = textLines
      .flatMapValues(value => value.toLowerCase().split("\\W+").toList)
      .groupBy((_, word) => word)(Grouped.`with`(Serdes.String, Serdes.String))

    // creates a discoverable named state store that can be queried interactively
    val count: KTable[String, Long] =
      groupedByWord.count()(Materialized.as(COUNT_STATE_STORE)(Serdes.String, Serdes.Long))

    val countStream = count.toStream
    countStream.print(Printed.toSysOut[String, Long].withLabel(APP_NAME))
    countStream.to(OUTPUT_TOPIC)(Produced.`with`(Serdes.String, Serdes.Long))

    builder.build()
  }

  // ugly/impure/blocking main thread ;-(
  private[this] def queryLoop(current: Int, streams: KafkaStreams): Int =
    current match {
      case c if c <= TIMEOUT_SEC =>
        val storeQuery: StoreQueryParameters[ReadOnlyKeyValueStore[String, Long]] =
          StoreQueryParameters.fromNameAndType(COUNT_STATE_STORE, QueryableStoreTypes.keyValueStore)
        val keyValueStore: ReadOnlyKeyValueStore[String, Long] =
          streams.store(storeQuery)

        val range: KeyValueIterator[String, Long] =
          keyValueStore.all()
        val counts: List[(String, Long)] =
          range.asScala.toList.map(keyValue => (keyValue.key, keyValue.value))

        println(s"counts: $counts")
        TimeUnit.SECONDS.sleep(1)

        queryLoop(c + 1, streams)
      case _ =>
        current
    }

  // sbt "interactive-queries/runMain com.kafka.demo.queries.WordCountInteractiveQueriesApp"
  def main(args: Array[String]): Unit = {
    val kafkaStreams = new KafkaStreams(topology, properties)
    kafkaStreams.start()
    logger.info(s"Start streaming app: $APP_NAME")

    queryLoop(0, kafkaStreams)

    kafkaStreams.close()
    logger.info(s"Stopping streaming app: $APP_NAME")
  }
}
