package com.kafka.demo.generic

import java.time.Duration

import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.KafkaConsumer.Conf
import com.typesafe.scalalogging.Logger
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer, KafkaAvroDeserializerConfig}
import org.apache.kafka.clients.consumer._

import scala.collection.JavaConverters.asJavaCollectionConverter
import scala.util.{Failure, Success, Try}

object Consumer {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val SCHEMA_REGISTRY_URL_VALUE = "http://localhost:8081"
  private[this] val TOPIC_NAME = "example.with-schema.customer"
  private[this] val GROUP_ID_VALUE = "consumer-specific"
  private[this] val TIMEOUT_MILLS = 100

  private[this] def newConsumer(): KafkaConsumer[AnyRef, AnyRef] =
    KafkaConsumer(Conf(
      new KafkaAvroDeserializer(),
      new KafkaAvroDeserializer(),
      BOOTSTRAP_SERVERS_VALUE,
      GROUP_ID_VALUE)
      .withProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getName)
      .withProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getName)
      .withProperty(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL_VALUE)
      .withProperty(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "false"))

  def main(args: Array[String]): Unit = {
    logger.info(s"Start to consume from $TOPIC_NAME")

    val consumer = newConsumer()
    consumer.subscribe(List(TOPIC_NAME).asJavaCollection)

    Try {
      while (true) {
        val records: ConsumerRecords[AnyRef, AnyRef] = consumer.poll(Duration.ofMillis(TIMEOUT_MILLS))
        records.iterator().forEachRemaining { record: ConsumerRecord[AnyRef, AnyRef] =>
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
        logger.info(s"Finish to consume from $TOPIC_NAME")
      case Failure(exception) =>
        logger.error(s"Finish to consume from $TOPIC_NAME with error", exception)
    }

    consumer.close()
  }

}
