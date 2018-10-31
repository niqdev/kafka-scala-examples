package com.kafka.demo
package simple

import java.time.Duration

import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.KafkaConsumer.Conf
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer => JKafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import scala.util.Try

// TODO open issue typo groupId in Wiki
/*
 * https://github.com/cakesolutions/scala-kafka-client/wiki/Scala-Kafka-Client#consumer
 */
object Consumer1 {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val TOPIC_NAME = "topic-no-schema-1"
  private[this] val GROUP_ID_VALUE = "consumer-1"
  private[this] val TIMEOUT_MILLS = 100

  private[this] def newConsumer(): JKafkaConsumer[String, String] = KafkaConsumer(
    Conf(new StringDeserializer(), new StringDeserializer(), bootstrapServers = BOOTSTRAP_SERVERS_VALUE, GROUP_ID_VALUE)
  )

  def main(args: Array[String]): Unit = {
    logger.info(s"Started to consume from $TOPIC_NAME")

    val consumer = newConsumer()
    Try {
      while (true) {
        val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(TIMEOUT_MILLS))
        records.iterator().forEachRemaining { record: ConsumerRecord[String, String] =>
          logger.info(
            s"""
               |message
               |  offset=${record.offset}
               |  partition=${record.partition}
               |  key=${record.key}
               |  value=${record.value}
           """.stripMargin)
        }
      }
    } map (_ => consumer.close())

    logger.info(s"Finished to consume from $TOPIC_NAME")
  }

}
