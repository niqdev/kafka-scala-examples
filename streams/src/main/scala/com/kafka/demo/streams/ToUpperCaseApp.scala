package com.kafka.demo
package streams

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.Logger
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.ImplicitConversions.{consumedFromSerde, producedFromSerde}
import org.apache.kafka.streams.scala.Serdes.String
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

/*
 * https://github.com/bbejeck/kafka-streams-in-action/blob/master/src/main/java/bbejeck/chapter_3/KafkaStreamsYellingApp.java
 */
object ToUpperCaseApp {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val APP_NAME = "to-upper-case-app"
  protected[streams] val IN_TOPIC = s"example.$APP_NAME.input"
  protected[streams] val OUT_TOPIC = s"example.$APP_NAME.output"
  private[this] val TIMEOUT_SEC = 60

  protected[streams] def properties: Properties = {
    val props = new Properties()
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_VALUE)
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_NAME)
    props
  }

  protected[streams] def topology: Topology = {
    // build processor topology
    val builder = new StreamsBuilder()
    // source node
    // ImplicitConversions.consumedFromSerde + Consumed[Serdes.String, Serdes.String]
    val inStream = builder.stream(IN_TOPIC)

    val upperCaseStream = inStream.mapValues(_.toUpperCase())

    // sink node
    // ImplicitConversions.producedFromSerde + Produced[Serdes.String, Serdes.String]
    upperCaseStream.to(OUT_TOPIC)
    upperCaseStream.print(Printed.toSysOut[java.lang.String, java.lang.String].withLabel(APP_NAME))

    builder.build()
  }

  def main(args: Array[String]): Unit = {
    val kafkaStreams = new KafkaStreams(topology, properties)
    kafkaStreams.start()
    logger.info(s"Start streaming app: $APP_NAME")

    TimeUnit.SECONDS.sleep(TIMEOUT_SEC)
    kafkaStreams.close()
    logger.info(s"Stopping streaming app: $APP_NAME")
  }

}
