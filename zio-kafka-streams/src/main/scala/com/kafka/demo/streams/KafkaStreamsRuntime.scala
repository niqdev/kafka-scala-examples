package com.kafka.demo
package streams

import com.kafka.demo.settings.Settings
import com.kafka.demo.streams.KafkaStreamsTopology.KafkaStreamsTopology
import org.apache.kafka.streams.KafkaStreams
import zio._
import zio.config.{ ZConfig, config }
import zio.logging.{ Logging, log }

object KafkaStreamsRuntime {
  type KafkaStreamsRuntime = Has[KafkaStreamsRuntime.Service]

  trait Service {
    def run: Task[Unit]
  }
  object Service {
    val live2 = new Service {
      override def run: Task[Unit] = ???
      //ZIO.bracket(setup)(stop)(start)
    }
    val live: ZIO[Logging with ZConfig[Settings] with KafkaStreamsTopology, Throwable, Unit] =
      ZManaged.make(setup)(stop).use(start)
  }

  private[this] def setup
    : ZIO[Logging with ZConfig[Settings] with KafkaStreamsTopology, Throwable, KafkaStreams] =
    for {
      _            <- log.info("Setup runtime ...")
      settings     <- config[Settings]
      topology     <- KafkaStreamsTopology.build
      kafkaStreams <- ZIO.effect(new KafkaStreams(topology, settings.properties))
    } yield kafkaStreams

  // TODO effectAsync
  private[this] def start: KafkaStreams => RIO[Logging, Unit] =
    kafkaStreams =>
      for {
        _ <- log.info("Start runtime ...")
        _ <- ZIO.effect(kafkaStreams.start())
      } yield ()

  // TODO retry
  // TODO catchAll ??? release accepts URIO i.e. convert Throwable to Nothing
  private[this] def stop: KafkaStreams => URIO[Logging, Unit] =
    kafkaStreams =>
      (for {
        _ <- log.info("Stop runtime ...")
        _ <- ZIO.effect(kafkaStreams.close(java.time.Duration.ofSeconds(1)))
      } yield ()).catchAll(_ => ZIO.succeed())

  val live: ZLayer[Logging with ZConfig[Settings] with KafkaStreamsTopology, Throwable, ZConfig[Unit]] =
    ZLayer.fromEffect(Service.live)
  val live2 = ZLayer.succeed(Service.live2)

  def run: RIO[KafkaStreamsRuntime, Unit] =
    ZIO.accessM[KafkaStreamsRuntime](_.get.run)

}
