package com.kafka.demo

import org.apache.kafka.streams.scala.kstream.{ Consumed, Produced }

trait RecordConsumed[K, V] {
  def consumed: Consumed[K, V]
}

object RecordConsumed {

  def apply[K, V](implicit C: RecordConsumed[K, V]): RecordConsumed[K, V] = C

  implicit def recordConsumed[K, V](implicit K: Codec[K], V: Codec[V]): RecordConsumed[K, V] =
    new RecordConsumed[K, V] {
      override def consumed: Consumed[K, V] =
        Consumed.`with`[K, V](K.serde, V.serde)
    }

}

trait AvroRecordProduced[K, V] {
  def produced(schemaRegistry: String): Produced[K, V]
}

object AvroRecordProduced {

  def apply[K, V](implicit P: AvroRecordProduced[K, V]): AvroRecordProduced[K, V] = P

  implicit def AvroRecordProduced[K: AvroCodec, V: AvroCodec]: AvroRecordProduced[K, V] =
    (schemaRegistry: String) =>
      Produced.`with`[K, V](
        implicitly[AvroCodec[K]].serde(schemaRegistry),
        implicitly[AvroCodec[V]].serde(schemaRegistry)
      )

}
