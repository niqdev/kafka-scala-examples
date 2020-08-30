package com.kafka.demo
package streams

import cats.effect.Sync
import com.kafka.demo.serialization.{ AvroRecordConsumed, AvroRecordMaterialized, AvroRecordProduced }
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.kstream.{ KStream, KTable }
import org.apache.kafka.streams.scala.{ ByteArrayKeyValueStore, StreamsBuilder }

final class StreamsBuilderOps[F[_]](private val builder: StreamsBuilder) extends AnyVal {

  def streamF[K, V](
    topic: Topic,
    schemaRegistry: UrlString,
    printDebug: Boolean = false
  )(implicit F: Sync[F], C: AvroRecordConsumed[K, V]): F[KStream[K, V]] =
    F.delay {
      val kstream = builder.stream(topic.value)(C.consumed(schemaRegistry))
      if (printDebug) kstream.print(Printed.toSysOut[K, V].withLabel(topic.value))
      kstream
    }
}

final class KStreamOps[F[_], K, V](private val kStream: KStream[K, V]) extends AnyVal {

  def toF(
    topic: Topic,
    schemaRegistry: UrlString,
    printDebug: Boolean = false
  )(implicit F: Sync[F], P: AvroRecordProduced[K, V]): F[Unit] =
    F.delay {
      if (printDebug) kStream.print(Printed.toSysOut[K, V].withLabel(topic.value))
      kStream.to(topic.value)(P.produced(schemaRegistry))
    }

  def toTable(schemaRegistry: UrlString)(
    implicit M: AvroRecordMaterialized[K, V, ByteArrayKeyValueStore]
  ): KTable[K, V] =
    kStream.toTable(M.materialize(schemaRegistry))

  def toTableF(schemaRegistry: UrlString)(
    implicit F: Sync[F],
    M: AvroRecordMaterialized[K, V, ByteArrayKeyValueStore]
  ): F[KTable[K, V]] =
    F.delay(toTable(schemaRegistry))

  def mapKey[KO](keyMapper: K => KO): KStream[KO, V] =
    kStream.map((key, value) => keyMapper(key) -> value)
}
