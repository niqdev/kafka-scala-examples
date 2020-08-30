package com.kafka.demo
package streams

import org.apache.kafka.streams.Topology
import zio.{ Has, RIO, ZLayer }

object KafkaStreamsTopology {
  type KafkaStreamsTopology = Has[KafkaStreamsTopology.Service]

  trait Service {
    def build: RIO[KafkaStreamsConfig, Topology]
  }

  def make: ZLayer[Nothing, Nothing, KafkaStreamsTopology] =
    ZLayer.succeed(new Service {
      override def build: RIO[KafkaStreamsConfig, Topology] = ???
    })

  // TODO
  def build: RIO[KafkaStreamsConfig, Topology] = ???
}
