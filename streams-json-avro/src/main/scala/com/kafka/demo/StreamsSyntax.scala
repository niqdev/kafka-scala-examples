package com.kafka.demo

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

    def kstream[K, V](topicName: String, printDebug: Boolean = false)(
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
  implicit final class KStreamOps[K, V](private val kstream: KStream[K, V]) {
    def toAvroTopic(
      topicName: String,
      schemaRegistry: String,
      printDebug: Boolean = false
    )(implicit P: AvroRecordProduced[K, V]): Unit = {
      if (printDebug) {
        kstream.print(Printed.toSysOut[K, V].withLabel(topicName))
      }
      kstream.to(topicName)(P.produced(schemaRegistry))
    }

  }

}
