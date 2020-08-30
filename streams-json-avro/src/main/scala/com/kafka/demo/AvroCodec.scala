package com.kafka.demo

import cats.syntax.either.catsSyntaxEitherObject
import com.sksamuel.avro4s.{ Decoder, Encoder, RecordFormat, SchemaFor }
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.{ GenericAvroSerde, SpecificAvroSerde }
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.Serdes

import scala.collection.JavaConverters.mapAsJavaMapConverter

trait AvroCodec[T] {
  def serde(schemaRegistry: String): Serde[T]
}

object AvroCodec extends GenericAvroCodec {

  def apply[T](implicit C: AvroCodec[T]): AvroCodec[T] = C

}

sealed trait GenericAvroCodec extends BaseAvroCodec {

  private def genericAvroSerializer[T: Encoder: Decoder: SchemaFor](
    schemaRegistry: String,
    isKey: Boolean = false
  ): (String, T) => Array[Byte] =
    (topic, data) =>
      genericAvroSerde(schemaRegistry, isKey)
        .serializer()
        .serialize(topic, RecordFormat[T].to(data))

  private def genericAvroDeserializer[T: Encoder: Decoder: SchemaFor](
    schemaRegistry: String,
    isKey: Boolean = false
  ): (String, Array[Byte]) => Option[T] =
    (topic, bytes) =>
      Either
        .catchNonFatal(genericAvroSerde(schemaRegistry, isKey).deserializer().deserialize(topic, bytes))
        .map(RecordFormat[T].from)
        .toOption

  implicit val keyAvroModelCodec: AvroCodec[KeyAvroModel] =
    (schemaRegistry: String) =>
      Serdes.fromFn(
        genericAvroSerializer[KeyAvroModel](schemaRegistry, isKey = true),
        genericAvroDeserializer[KeyAvroModel](schemaRegistry, isKey = true)
      )

  implicit val valueAvroModelCodec: AvroCodec[ValueAvroModel] =
    (schemaRegistry: String) =>
      Serdes.fromFn(
        genericAvroSerializer[ValueAvroModel](schemaRegistry),
        genericAvroDeserializer[ValueAvroModel](schemaRegistry)
      )

}

sealed trait BaseAvroCodec {

  /*
   * https://github.com/akka/alpakka-kafka/issues/342#issuecomment-402490030
   * https://www.madewithtea.com/kafka-streams-in-scala-with-schema-registry.html
   */
  private def schemaRegistryMap(schemaRegistry: String): java.util.Map[String, String] =
    Map(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> schemaRegistry).asJava

  protected def specificAvroSerde[T <: SpecificRecord](
    schemaRegistry: String,
    isKey: Boolean
  ): SpecificAvroSerde[T] = {
    val serde = new SpecificAvroSerde[T]()
    serde.configure(schemaRegistryMap(schemaRegistry), isKey)
    serde
  }

  protected def genericAvroSerde(
    schemaRegistry: String,
    isKey: Boolean
  ): GenericAvroSerde = {
    val serde = new GenericAvroSerde()
    serde.configure(schemaRegistryMap(schemaRegistry), isKey)
    serde
  }

}
