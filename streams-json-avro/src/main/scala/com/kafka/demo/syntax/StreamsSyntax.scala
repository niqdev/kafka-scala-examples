package com.kafka.demo.syntax

import com.kafka.demo.{ AvroRecordProduced, RecordConsumed }
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

final class StreamsBuilderOps(private val builder: StreamsBuilder) extends AnyVal {

  /**
    * Helper syntax to create a KStream[K, V] from a StreamsBuilder and print debug.
    */
  def kStream[K, V](topicName: String, printDebug: Boolean = false)(
    implicit C: RecordConsumed[K, V]
  ): KStream[K, V] = {
    val ks = builder.stream(topicName)(C.consumed)
    if (printDebug)
      ks.print(Printed.toSysOut[K, V].withLabel(topicName))
    ks
  }

}

final class KStreamOps[K, V](private val kStream: KStream[K, V]) extends AnyVal {

  /**
    * Helper syntax to write Avro GenericRecord to a topic from a KStream[K, V].
    */
  def toAvroTopic(
    topicName: String,
    schemaRegistry: String,
    printDebug: Boolean = false
  )(implicit P: AvroRecordProduced[K, V]): Unit = {
    if (printDebug)
      kStream.print(Printed.toSysOut[K, V].withLabel(topicName))
    kStream.to(topicName)(P.produced(schemaRegistry))
  }

}
