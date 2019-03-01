package com.kafka.demo

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.{GenericAvroSerde, SpecificAvroSerde}
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.kstream.Produced

import scala.collection.JavaConverters.mapAsJavaMapConverter

object AvroRecord {

  /*
   * https://github.com/akka/alpakka-kafka/issues/342#issuecomment-402490030
   * https://www.madewithtea.com/kafka-streams-in-scala-with-schema-registry.html
   */
  private def schemaRegistryMap(schemaRegistry: String): java.util.Map[String, String] =
    Map(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> schemaRegistry).asJava

  private def specificAvroSerde[T <: SpecificRecord](
    schemaRegistry: String,
    isKey: Boolean = false
  ): SpecificAvroSerde[T] = {
    val serde = new SpecificAvroSerde[T]()
    serde.configure(schemaRegistryMap(schemaRegistry), isKey)
    serde
  }

  private def genericAvroSerde(
    schemaRegistry: String,
    isKey: Boolean = false
  ): GenericAvroSerde = {
    val serde = new GenericAvroSerde()
    serde.configure(schemaRegistryMap(schemaRegistry), isKey)
    serde
  }

  def avroSpecificRecordProduced[K <: SpecificRecord, V <: SpecificRecord](
    schemaRegistry: String
  ): Produced[K, V] =
    Produced.`with`[K, V](
      specificAvroSerde[K](schemaRegistry, isKey = true),
      specificAvroSerde[V](schemaRegistry)
    )

  def avroGenericRecordProduced(schemaRegistry: String): Produced[GenericRecord, GenericRecord] =
    Produced.`with`[GenericRecord, GenericRecord](
      genericAvroSerde(schemaRegistry, isKey = true),
      genericAvroSerde(schemaRegistry)
    )

}
