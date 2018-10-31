package com.kafka.demo.cakesolutions

import java.time.LocalDateTime

import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{KafkaProducer, KafkaProducerRecord}
import com.typesafe.scalalogging.Logger
import org.apache.kafka.common.serialization.StringSerializer

/*
 * https://github.com/cakesolutions/scala-kafka-client/wiki/Scala-Kafka-Client#producer
 */
object Producer {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val TOPIC_NAME = "topic-no-schema-1"

  private[this] def newProducer(): KafkaProducer[String, String] = KafkaProducer(
    Conf(new StringSerializer(), new StringSerializer(), bootstrapServers = BOOTSTRAP_SERVERS_VALUE)
  )

  def main(args: Array[String]): Unit = {
    logger.info(s"Started to produce on $TOPIC_NAME")

    val producer = newProducer()

    (1 to 100)
      .map { i =>
        (i, s"Message $i @ ${LocalDateTime.now} on ${Thread.currentThread.getName}")
      }
      .map {
        case (i, message) => KafkaProducerRecord(TOPIC_NAME, s"$i", message)
      }
      .foreach(producer.send)

    producer.close()

    logger.info(s"Finished to produce on $TOPIC_NAME")
  }

}
