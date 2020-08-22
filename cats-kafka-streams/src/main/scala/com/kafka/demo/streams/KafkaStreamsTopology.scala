package com.kafka.demo.streams

import cats.effect.Sync
import com.kafka.demo.settings.Settings
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.scala.StreamsBuilder

sealed abstract class KafkaStreamsTopology[F[_] : Sync] {

  def build: Settings => F[Topology] =
    settings =>
      Sync[F].delay {
        val builder = new StreamsBuilder()
        // TODO
        builder.build()
      }
}

object KafkaStreamsTopology {
  def apply[F[_] : Sync]: KafkaStreamsTopology[F] =
    new KafkaStreamsTopology[F] {}
}
