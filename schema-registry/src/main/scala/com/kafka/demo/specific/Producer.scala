package com.kafka.demo.specific

import java.util.Properties

import com.typesafe.scalalogging.Logger
import io.confluent.examples.clients.basicavro.Payment
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroSerializer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

/*
 * https://github.com/confluentinc/examples/blob/5.0.x/clients/avro/src/main/java/io/confluent/examples/clients/basicavro/ProducerExample.java
 */
object Producer {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val SCHEMA_REGISTRY_URL_VALUE = "http://localhost:8081"
  private[this] val TOPIC_NAME = "topic-schema-payment"

  private[this] def newProducer(): KafkaProducer[String, Payment] = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_VALUE)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getName)
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL_VALUE)
    new KafkaProducer(props)
  }

  def main(args: Array[String]): Unit = {
    logger.info(s"Start to produce on $TOPIC_NAME")

    val producer = newProducer()

    (1 to 10)
      .map { i =>
        val orderId = s"id$i"
        val payment = Payment(orderId, 1000.00d)
        val record = new ProducerRecord[String, Payment](TOPIC_NAME, payment.id, payment)
        record
      }
      .foreach(producer.send)

    producer.close()

    logger.info(s"Finish to produce on $TOPIC_NAME")
  }
}
