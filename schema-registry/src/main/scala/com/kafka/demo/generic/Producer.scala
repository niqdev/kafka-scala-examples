package com.kafka.demo.generic

import cakesolutions.kafka.KafkaProducer
import cakesolutions.kafka.KafkaProducer.Conf
import com.typesafe.scalalogging.Logger
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroSerializer}
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}

import scala.util.{Failure, Success}

/*
 * https://docs.confluent.io/current/streams/developer-guide/datatypes.html#avro
 * https://github.com/confluentinc/kafka-streams-examples/blob/5.0.x/src/test/scala/io/confluent/examples/streams/GenericAvroScalaIntegrationTest.scala
 */
object Producer {
  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val BOOTSTRAP_SERVERS_VALUE = "localhost:9092"
  private[this] val SCHEMA_REGISTRY_URL_VALUE = "http://localhost:8081"
  private[this] val TOPIC_NAME = "example.with-schema.customer"

  private[this] val SCHEMA_CUSTOMER_V1 = "/customer_v1.avsc"
  private[this] val SCHEMA_CUSTOMER_V2 = "/customer_v2.avsc"

  private[this] val schemaCustomerV1 =
    new Schema.Parser().parse(getClass.getResourceAsStream(SCHEMA_CUSTOMER_V1))
  private[this] val schemaCustomerV2 =
    new Schema.Parser().parse(getClass.getResourceAsStream(SCHEMA_CUSTOMER_V2))

  private[this] def newProducer(): KafkaProducer[String, GenericRecord] =
    KafkaProducer(Conf(
      new KafkaAvroSerializer(),
      new KafkaAvroSerializer(),
      BOOTSTRAP_SERVERS_VALUE)
      .withProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getName)
      .withProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getName)
      .withProperty(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL_VALUE))
      .asInstanceOf[KafkaProducer[String, GenericRecord]]

  // prefer avro4s approach
  def main(args: Array[String]): Unit = {
    logger.info(s"Start to produce on $TOPIC_NAME")

    val producer = newProducer()

    Seq(
      {
        val customer = new GenericData.Record(schemaCustomerV1)
        customer.put("name", "name-1")
        ("id-1", customer)
      },
      {
        val customer = new GenericData.Record(schemaCustomerV2)
        customer.put("name", "name-2")
        ("id-2", customer)
      })
      .map {
        case (key: String, customer: GenericRecord) =>
          new ProducerRecord[String, GenericRecord](TOPIC_NAME, key, customer)
      }
      .foreach { record =>
        logger.info(s"record: $record")
        producer.send(record).onComplete {
          case Success(recordMetadata) =>
            logger.info(s"recordMetadata timestamp: ${recordMetadata.timestamp()}")
          case Failure(exception) =>
            logger.error(s"error: $exception")
        }(scala.concurrent.ExecutionContext.global)
      }

    producer.close()

    logger.info(s"Finish to produce on $TOPIC_NAME")
  }

}
