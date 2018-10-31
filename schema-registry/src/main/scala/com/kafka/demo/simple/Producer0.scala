package com.kafka.demo
package simple

import java.time.LocalDateTime
import java.util.Properties

import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.ProducerConfig._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

/*
 * https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
 */
object Producer0 extends App {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val TOPIC_NAME = "topic-0"

  private[this] def newProducer(): KafkaProducer[String, String] = {
    val props = new Properties()
    props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_VALUE)
    props.put(ACKS_CONFIG, "all")
    props.put(RETRIES_CONFIG, 0)
    props.put(BATCH_SIZE_CONFIG, 16384)
    props.put(LINGER_MS_CONFIG, 1)
    props.put(BUFFER_MEMORY_CONFIG, 33554432)
    props.put(KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    new KafkaProducer(props)
  }

  def main: Unit = {
    logger.info(s"Started to produce on $TOPIC_NAME")

    val producer = newProducer()
    for {
      i <- 100
      message <- s"Message $i @ ${LocalDateTime.now} on ${Thread.currentThread.getName}"
      record <- new ProducerRecord(TOPIC_NAME, s"$i", message)
    } yield {
      producer.send(record)
    }
    producer.close()

    logger.info(s"Finished to produce on $TOPIC_NAME")
  }

}
