package com.kafka.demo

import _root_.cakesolutions.kafka.testkit.KafkaServer
import io.confluent.kafka.schemaregistry.client.{MockSchemaRegistryClient, SchemaRegistryClient}
import io.confluent.kafka.serializers.{AbstractKafkaAvroSerDeConfig, KafkaAvroDeserializer, KafkaAvroDeserializerConfig, KafkaAvroSerializer}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import org.scalatest.{BeforeAndAfterAll, TestSuite}

import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.util.Random

/*
 * https://stackoverflow.com/questions/39670691/how-to-have-kafkaproducer-to-use-a-mock-schema-registry-for-testing
 * https://stackoverflow.com/questions/52737242/how-can-do-functional-tests-for-kafka-streams-with-avro-schemaregistry
 *
 * https://github.com/confluentinc/kafka-streams-examples/blob/5.0.0-post/src/test/java/io/confluent/examples/streams/kafka/EmbeddedSingleNodeKafkaCluster.java
 * https://github.com/confluentinc/schema-registry/blob/master/avro-serializer/src/test/java/io/confluent/kafka/serializers/KafkaAvroSerializerTest.java
 */
trait BaseKafkaSpec extends BeforeAndAfterAll {
  this: TestSuite =>

  private[this] val kafkaServer: KafkaServer = new KafkaServer()
  private[this] val mockSchemaRegistryClient = new MockSchemaRegistryClient()

  override def beforeAll(): Unit =
    kafkaServer.startup()

  override def afterAll(): Unit =
    kafkaServer.close()

  protected[this] def randomString: String =
    Random.alphanumeric.take(5).mkString("")

  private[this] def serializer[T](client: SchemaRegistryClient, isKey: Boolean = false): Serializer[T] = {
    val configs = Map(
      AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "none",
      AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS -> "true"
    )
    val serializer = new KafkaAvroSerializer(client)
    serializer.configure(configs.asJava, isKey)
    serializer.asInstanceOf[Serializer[T]]
  }

  protected[this] def produce(topic: String,
                              records: Iterable[(AnyRef, AnyRef)]): Unit =
    kafkaServer.produce(
      topic,
      records.map(record => new ProducerRecord(topic, record._1, record._2)),
      serializer(mockSchemaRegistryClient, isKey = true),
      serializer(mockSchemaRegistryClient),
      Map(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer].getName,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer].getName
      ))

  private[this] def deserializer[T](client: SchemaRegistryClient, isKey: Boolean = false): Deserializer[T] = {
    val configs = Map(
      AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "none",
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> "true"
    )
    val deserializer = new KafkaAvroDeserializer(client)
    deserializer.configure(configs.asJava, isKey)
    deserializer.asInstanceOf[Deserializer[T]]
  }

  protected[this] def consume[K, V](topic: String,
                              size: Int = 0,
                              timeoutMills: Long = 1000): Seq[(Option[K], V)] =
    kafkaServer.consume(
      topic,
      size,
      timeoutMills,
      deserializer[K](mockSchemaRegistryClient, isKey = true),
      deserializer[V](mockSchemaRegistryClient),
      Map(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroDeserializer].getName,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroDeserializer].getName,
        ConsumerConfig.GROUP_ID_CONFIG -> randomString
      ))

}
