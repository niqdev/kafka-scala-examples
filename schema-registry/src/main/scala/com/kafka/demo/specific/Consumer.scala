package com.kafka.demo.specific

import java.time.Duration
import java.util.Properties

import com.typesafe.scalalogging.Logger
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer, KafkaAvroDeserializerConfig}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}

import scala.collection.JavaConverters.asJavaCollectionConverter
import scala.util.{Failure, Success, Try}

/*
 * https://github.com/confluentinc/examples/blob/5.0.x/clients/avro/src/main/java/io/confluent/examples/clients/basicavro/ConsumerExample.java
 */
object Consumer {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val SCHEMA_REGISTRY_URL_VALUE = "http://localhost:8081"
  private[this] val TOPIC_NAME = "example.with-schema.payment"
  private[this] val GROUP_ID_VALUE = "consumer-specific"
  private[this] val TIMEOUT_MILLS = 100

  private[this] def newConsumer(): KafkaConsumer[String, KafkaAvroDeserializer] = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_VALUE)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID_VALUE)
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getName)
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL_VALUE)
    props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")
    new KafkaConsumer(props)
  }

  def main(args: Array[String]): Unit = {
    logger.info(s"Start to consume from $TOPIC_NAME")

    val consumer = newConsumer()
    consumer.subscribe(List(TOPIC_NAME).asJavaCollection)

    Try {
      while (true) {
        val records: ConsumerRecords[String, KafkaAvroDeserializer] = consumer.poll(Duration.ofMillis(TIMEOUT_MILLS))
        records.iterator().forEachRemaining { record: ConsumerRecord[String, KafkaAvroDeserializer] =>
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
