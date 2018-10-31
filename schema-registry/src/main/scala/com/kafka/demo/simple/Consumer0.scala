package com.kafka.demo
package simple

import java.time.Duration
import java.util.Properties

import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.ConsumerConfig._
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConverters.asJavaCollectionConverter
import scala.util.{Failure, Success, Try}

/*
 * https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
 */
object Consumer0 {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val TOPIC_NAME = "topic-no-schema-0"
  private[this] val GROUP_ID_VALUE = "consumer-0"
  private[this] val TIMEOUT_MILLS = 100

  private[this] def newConsumer(): KafkaConsumer[String, String] = {
    val props = new Properties()
    props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_VALUE)
    props.put(GROUP_ID_CONFIG, GROUP_ID_VALUE)
    props.put(ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "100")
    props.put(KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    new KafkaConsumer(props)
  }

  def main(args: Array[String]): Unit = {
    logger.info(s"Started to consume from $TOPIC_NAME")

    val consumer = newConsumer()
    consumer.subscribe(List(TOPIC_NAME).asJavaCollection)
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
    } match {
      case Success(_) =>
        logger.info(s"Finished to consume from $TOPIC_NAME")
      case Failure(exception) =>
        logger.error(s"Finished to consume from $TOPIC_NAME with error", exception)
    }

    consumer.close()
  }

}
