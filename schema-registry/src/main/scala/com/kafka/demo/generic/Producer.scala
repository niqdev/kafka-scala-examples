package com.kafka.demo.generic

import cakesolutions.kafka.KafkaProducer
import cakesolutions.kafka.KafkaProducer.Conf
import com.typesafe.scalalogging.Logger
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroSerializer}
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}

/*
 * https://docs.confluent.io/current/streams/developer-guide/datatypes.html#avro
 * https://github.com/confluentinc/kafka-streams-examples/blob/5.0.x/src/test/scala/io/confluent/examples/streams/GenericAvroScalaIntegrationTest.scala
 */
object Producer {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val SCHEMA_REGISTRY_URL_VALUE = "http://localhost:8081"
  private[this] val SCHEMA_FILE = "/customer_v1.avsc"
  private[this] val TOPIC_NAME = "example.with-schema.customer"

  private[this] def newProducer(): KafkaProducer[AnyRef, AnyRef] =
    KafkaProducer(Conf(
      new KafkaAvroSerializer(),
      new KafkaAvroSerializer(),
      BOOTSTRAP_SERVERS_VALUE)
      .withProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getName)
      .withProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getName)
      .withProperty(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL_VALUE))

  def main(args: Array[String]): Unit = {
    logger.info(s"Start to produce on $TOPIC_NAME")

    // prefer avro4s approach
    val schema = new Schema.Parser().parse(getClass.getResourceAsStream(SCHEMA_FILE))
    val producer = newProducer()

    (1 to 10)
      .map { i =>
        val customer = new GenericData.Record(schema)
        customer.put("name", s"name-$i")
        val key = s"id-$i"
        new ProducerRecord[AnyRef, AnyRef](TOPIC_NAME, key, customer)
      }
      .foreach { record =>
        producer.send(record)
      }

    producer.close()

    logger.info(s"Finish to produce on $TOPIC_NAME")
  }
}
