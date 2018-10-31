package com.kafka.demo
package simple

import java.time.LocalDateTime

import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{KafkaProducer, KafkaProducerRecord}
import com.typesafe.scalalogging.Logger
import org.apache.kafka.common.serialization.StringSerializer

/*
 * https://github.com/cakesolutions/scala-kafka-client/wiki/Scala-Kafka-Client#producer
 */
object Producer1 extends App {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val TOPIC_NAME = "topic-1"

  private[this] def newProducer(): KafkaProducer[String, String] = KafkaProducer(
    Conf(new StringSerializer(), new StringSerializer(), bootstrapServers = BOOTSTRAP_SERVERS_VALUE)
  )

  def main: Unit = {
    logger.info(s"Started to produce on $TOPIC_NAME")

    val producer = newProducer()
    for {
      i <- 100
      message <- s"Message $i @ ${LocalDateTime.now} on ${Thread.currentThread.getName}"
      record <- KafkaProducerRecord(TOPIC_NAME, s"$i", message)
    } yield {
      producer.send(record)
    }
    producer.close()

    logger.info(s"Finished to produce on $TOPIC_NAME")
  }

}
