package com.kafka.demo
package streams

import zio._
import zio.logging.{ Logger, Logging }

object KafkaStreamsTopology {
  type KafkaStreamsTopology = Has[KafkaStreamsTopology.Service]

  trait Service {
    // TODO return org.apache.kafka.streams.Topology
    def build: UIO[Unit]
  }
  object Service {
    val live: Logger[String] => Service =
      log =>
        new Service {
          override def build: UIO[Unit] =
            log.info("TODO topology")
        }
  }

  val live: ZLayer[Logging, Nothing, KafkaStreamsTopology] =
    ZLayer.fromService(Service.live)

  def build: URIO[KafkaStreamsTopology, Unit] =
    ZIO.accessM[KafkaStreamsTopology](_.get.build)

}
