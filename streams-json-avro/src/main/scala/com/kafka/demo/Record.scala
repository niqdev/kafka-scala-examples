package com.kafka.demo

import org.apache.kafka.streams.scala.kstream.{Consumed, Produced}

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

trait RecordProduced[K, V] {
  def produced: Produced[K, V]
}

object RecordProduced {

  def apply[K, V](implicit P: RecordProduced[K, V]): RecordProduced[K, V] = P

  implicit def recordProduced[K: Codec, V: Codec]: RecordProduced[K, V] =
    new RecordProduced[K, V] {
      override def produced: Produced[K, V] =
        Produced.`with`[K, V](implicitly[Codec[K]].serde, implicitly[Codec[V]].serde)
    }

}
