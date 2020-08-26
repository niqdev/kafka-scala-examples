package com.kafka.demo
package serialization

import org.apache.kafka.streams.scala.kstream.{Consumed, Produced}

/**
 * See org.apache.kafka.streams.kstream.Consumed
 */
trait AvroRecordConsumed[K, V] {
  def consumed(schemaRegistry: UrlString): Consumed[K, V]
}

object AvroRecordConsumed {
  def apply[K, V](implicit ev: AvroRecordConsumed[K, V]): AvroRecordConsumed[K, V] = ev

  implicit def avroRecordConsumed[K, V](
    implicit K: AvroCodec[K],
    V: AvroCodec[V]
  ): AvroRecordConsumed[K, V] =
    schemaRegistry => Consumed.`with`[K, V](K.serde(schemaRegistry), V.serde(schemaRegistry))
}

/**
 * See org.apache.kafka.streams.kstream.Produced
 */
trait AvroRecordProduced[K, V] {
  def produced(schemaRegistry: UrlString): Produced[K, V]
}

object AvroRecordProduced {
  def apply[K, V](implicit ev: AvroRecordProduced[K, V]): AvroRecordProduced[K, V] = ev

  implicit def avroRecordProduced[K, V](
    implicit K: AvroCodec[K],
    V: AvroCodec[V]
  ): AvroRecordProduced[K, V] =
    schemaRegistry => Produced.`with`[K, V](K.serde(schemaRegistry), V.serde(schemaRegistry))
}
