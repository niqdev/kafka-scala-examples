package com.kafka.demo

import com.sksamuel.avro4s.{Decoder, Encoder, RecordFormat, SchemaFor}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

object StreamsSyntax {

  /**
    * Helper syntax to create a KStream[K, V] from a StreamsBuilder and print debug.
    *
    * @param printDebug
    * @param topicName
    */
  implicit final class StreamsBuilderOps(private val builder: StreamsBuilder) extends AnyVal {

    def kstream[K, V](printDebug: Boolean = false)(topicName: String)(
      implicit C: RecordConsumed[K, V]
    ): KStream[K, V] = {
      val ks = builder.stream(topicName)(C.consumed)
      if (printDebug) {
        ks.print(Printed.toSysOut[K, V].withLabel(topicName))
      }
      ks
    }

  }

  /**
    * Helper syntax to write Avro GenericRecord to a topic from a KStream[K, V].
    *
    * @param schemaRegistry
    * @param topicName
    */
  implicit final class KStreamOps[
  K: Encoder : Decoder : SchemaFor,
  V: Encoder : Decoder : SchemaFor
  ](private val kstream: KStream[K, V]) {

    def toAvroGenericTopic(
      schemaRegistry: String,
      printDebug: Boolean = false
    )(topicName: String): Unit = {
      if (printDebug) {
        kstream.print(Printed.toSysOut[K, V].withLabel(topicName))
      }

      kstream.map { (key, value) =>
        (
          RecordFormat[K].to(key).asInstanceOf[GenericRecord],
          RecordFormat[V].to(value).asInstanceOf[GenericRecord]
        )
      }.to(topicName)(AvroRecord.avroGenericRecordProduced(schemaRegistry))
    }

  }

}
