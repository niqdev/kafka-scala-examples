package com.kafka.demo.cakesolutions

import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{ KafkaProducer, KafkaProducerRecord }
import com.kafka.demo.KafkaHelper
import com.typesafe.scalalogging.Logger
import org.apache.kafka.common.serialization.StringSerializer

/*
 * https://github.com/cakesolutions/scala-kafka-client/wiki/Scala-Kafka-Client#producer
 */
object Producer {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val TOPIC_NAME              = "example.no-schema.cakesolutions"

  private[cakesolutions] def newProducer(bootstrapServers: String): KafkaProducer[String, String] =
    KafkaProducer(Conf(new StringSerializer(), new StringSerializer(), bootstrapServers))

  def main(args: Array[String]): Unit = {
    logger.info(s"Start to produce on $TOPIC_NAME")

    val producer = newProducer(BOOTSTRAP_SERVERS_VALUE)

    KafkaHelper
      .produceMessages {
        case (i, message) => KafkaProducerRecord(TOPIC_NAME, s"$i", message)
      }
      .foreach(producer.send)

    producer.close()

    logger.info(s"Finish to produce on $TOPIC_NAME")
  }

}
