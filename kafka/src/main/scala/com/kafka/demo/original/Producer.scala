package com.kafka.demo.original

import java.util.Properties

import com.kafka.demo.KafkaHelper
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.ProducerConfig._
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }
import org.apache.kafka.common.serialization.StringSerializer

/*
 * https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
 */
object Producer {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val TOPIC_NAME              = "example.no-schema.original"

  private[this] def newProducer(): KafkaProducer[String, String] = {
    val props = new Properties()
    props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_VALUE)
    props.put(ACKS_CONFIG, "all")
    props.put(RETRIES_CONFIG, "0")
    props.put(BATCH_SIZE_CONFIG, "16384")
    props.put(LINGER_MS_CONFIG, "1")
    props.put(BUFFER_MEMORY_CONFIG, "33554432")
    props.put(KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    new KafkaProducer(props)
  }

  def main(args: Array[String]): Unit = {
    logger.info(s"Start to produce on $TOPIC_NAME")

    val producer = newProducer()

    KafkaHelper
      .produceMessages { case (i, message) =>
        new ProducerRecord(TOPIC_NAME, s"$i", message)
      }
      .foreach(producer.send)

    producer.close()

    logger.info(s"Finish to produce on $TOPIC_NAME")
  }

}
