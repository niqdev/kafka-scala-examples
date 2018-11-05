package com.kafka.demo.cakesolutions

import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.KafkaConsumer.Conf
import com.kafka.demo.KafkaHelper
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.{KafkaConsumer => JKafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

/*
 * https://github.com/cakesolutions/scala-kafka-client/wiki/Scala-Kafka-Client#consumer
 */
object Consumer {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val TOPIC_NAME = "topic-no-schema-cakesolutions"
  private[this] val GROUP_ID_VALUE = "consumer-cakesolutions"
  private[this] val TIMEOUT_MILLS = 100

  private[this] def newConsumer(): JKafkaConsumer[String, String] = KafkaConsumer(
    Conf(new StringDeserializer(), new StringDeserializer(), bootstrapServers = BOOTSTRAP_SERVERS_VALUE, GROUP_ID_VALUE)
  )

  def main(args: Array[String]): Unit =
    KafkaHelper.consume(newConsumer(), TOPIC_NAME, TIMEOUT_MILLS)

}
