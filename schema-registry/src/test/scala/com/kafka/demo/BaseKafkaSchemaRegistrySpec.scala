package com.kafka.demo

import _root_.cakesolutions.kafka.testkit.KafkaServer
import io.confluent.kafka.schemaregistry.client.{MockSchemaRegistryClient, SchemaRegistryClient}
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer, KafkaAvroDeserializerConfig, KafkaAvroSerializer}
import org.apache.kafka.clients.consumer.{ConsumerConfig, OffsetResetStrategy}
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}
import org.scalatest.{BeforeAndAfterAll, TestSuite}

import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.util.Random

/*
 * // examples
 * https://github.com/confluentinc/kafka-streams-examples/blob/5.0.x/src/test/java/io/confluent/examples/streams/kafka/EmbeddedSingleNodeKafkaCluster.java
 * https://github.com/confluentinc/schema-registry/blob/master/avro-serializer/src/test/java/io/confluent/kafka/serializers/KafkaAvroSerializerTest.java
 *
 * // issues
 * https://groups.google.com/forum/#!topic/confluent-platform/_pJ1g9g1woY
 * https://github.com/confluentinc/kafka-streams-examples/issues/26
 *
 * // alternatives
 * https://github.com/manub/scalatest-embedded-kafka
 * https://github.com/jpzk/mockedstreams
 */
trait BaseKafkaSchemaRegistrySpec extends BeforeAndAfterAll {
  this: TestSuite =>

  private[this] val kafkaServer: KafkaServer = new KafkaServer()
  private[this] val schemaRegistry: SchemaRegistryClient = new MockSchemaRegistryClient()

  override def beforeAll(): Unit =
    kafkaServer.startup()

  override def afterAll(): Unit =
    kafkaServer.close()

  protected[this] def randomString: String =
    Random.alphanumeric.take(5).mkString("")

  private[this] def serializer(client: SchemaRegistryClient,
                               isKey: Boolean = false): KafkaAvroSerializer = {
    val configs = Map(
      AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "none",
      AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS -> "true"
    )
    val serializer = new KafkaAvroSerializer(client)
    serializer.configure(configs.asJava, isKey)
    serializer
  }

  protected[this] def produce(topic: String,
                              records: Iterable[(Option[AnyRef], AnyRef)]): Unit =
    kafkaServer.produce(
      topic,
      records.map(record => new ProducerRecord(topic, record._1.get, record._2)),
      serializer(schemaRegistry, isKey = true),
      serializer(schemaRegistry),
      Map(
        ProducerConfig.ACKS_CONFIG -> "all",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer].getName,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer].getName
      ))

  private[this] def deserializer(client: SchemaRegistryClient,
                                 isSpecific: Boolean,
                                 isKey: Boolean = false): KafkaAvroDeserializer = {
    val configs = Map(
      AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "none",
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> isSpecific.toString
    )
    val deserializer = new KafkaAvroDeserializer(client)
    deserializer.configure(configs.asJava, isKey)
    deserializer
  }

  protected[this] def consume(topic: String,
                              size: Int = 0,
                              isSpecific: Boolean,
                              timeoutMills: Long = 1000): Seq[(Option[AnyRef], AnyRef)] =
    kafkaServer.consume(
      topic,
      size,
      timeoutMills,
      deserializer(schemaRegistry, isSpecific, isKey = true),
      deserializer(schemaRegistry, isSpecific),
      Map(
        ConsumerConfig.GROUP_ID_CONFIG -> "test",
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "true",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> OffsetResetStrategy.EARLIEST.toString.toLowerCase,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroDeserializer].getName,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroDeserializer].getName
      ))

}
