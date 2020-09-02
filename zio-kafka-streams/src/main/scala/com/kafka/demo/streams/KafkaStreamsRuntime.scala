package com.kafka.demo
package streams

import com.kafka.demo.settings.Settings
import zio._
import zio.logging._

object KafkaStreamsRuntime {
  type KafkaStreamsRuntime = Has[KafkaStreamsRuntime.Service]

  trait Service {
    def run: UIO[Unit]
  }
  object Service {
    val live: (Logger[String], Settings, KafkaStreamsTopology.Service) => Service =
      (log, settings, topology) =>
        new Service {
          override def run: UIO[Unit] =
            for {
              _ <- log.info("TODO runtime")
              _ <- log.info(s"bootstrapServers=${settings.bootstrapServers}")
              _ <- topology.build
            } yield ()
        }
  }

  // ZLayer[Logging with ZConfig[Settings] with KafkaStreamsTopology, Nothing, KafkaStreamsRuntime]
  val live =
    ZLayer.fromServices(Service.live)

  def run: URIO[KafkaStreamsRuntime, Unit] =
    ZIO.accessM[KafkaStreamsRuntime](_.get.run)

}
