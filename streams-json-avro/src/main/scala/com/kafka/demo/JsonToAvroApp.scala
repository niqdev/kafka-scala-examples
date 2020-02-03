package com.kafka.demo

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.kafka.demo.syntax._
import com.typesafe.scalalogging.Logger
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

object JsonToAvroApp {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val APPLICATION_NAME = "streams-json-to-avro-app"
  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val SCHEMA_REGISTRY_URL_VALUE = "http://localhost:8081"
  private[this] val INPUT_TOPIC = s"json.$APPLICATION_NAME.input"
  private[this] val OUTPUT_TOPIC = s"avro.$APPLICATION_NAME.output"
  private[this] val TIMEOUT_SEC = 60

  protected[demo] def buildProperties(applicationName: String, bootstrapServers: String, schemaRegistryUrl: String): Properties = {
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationName)
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
    props
  }

  // [String, JsonModel] >>> GenericRecord: [KeyAvroModel, ValueAvroModel]
  protected[demo] def buildTopology(schemaRegistryUrl: String, inputTopic: String, outputTopic: String): Topology = {
    val builder = new StreamsBuilder()

    builder
      .kStream[String, JsonModel](inputTopic, printDebug = true)
      .map((keyString, jsonModel) => (KeyAvroModel(keyString), ValueAvroModel(jsonModel.valueInt, jsonModel.valueString.toUpperCase)))
      .toAvroTopic(outputTopic, schemaRegistryUrl, printDebug = true)

    builder.build()
  }

  def main(args: Array[String]): Unit = {
    val topology = buildTopology(SCHEMA_REGISTRY_URL_VALUE, INPUT_TOPIC, OUTPUT_TOPIC)
    val properties = buildProperties(APPLICATION_NAME, BOOTSTRAP_SERVERS_VALUE, SCHEMA_REGISTRY_URL_VALUE)
    val kafkaStreams = new KafkaStreams(topology, properties)
    kafkaStreams.start()
    logger.info(s"Start streaming app: $APPLICATION_NAME")

    TimeUnit.SECONDS.sleep(TIMEOUT_SEC)
    kafkaStreams.close()
    logger.info(s"Stopping streaming app: $APPLICATION_NAME")
  }

}
