package com.kafka.demo

import com.kafka.demo.settings.Settings
import com.kafka.demo.streams.{ KafkaStreamsRuntime, KafkaStreamsTopology }
import zio._
import zio.config.{ ZConfig, config }
import zio.logging._

// sbt "zio-kafka-streams/runMain com.kafka.demo.ZioKafkaStreamsApp"
object ZioKafkaStreamsApp extends App {

  private[this] final val configLocalLayer = ZConfig.fromMap(
    Map(
      "APPLICATION_NAME"    -> "zio-kafka-streams",
      "BOOTSTRAP_SERVERS"   -> "localhost:9092",
      "SCHEMA_REGISTRY_URL" -> "http://localhost:8081",
      "SOURCE_TOPIC"        -> "zio.source.v1",
      "SINK_TOPIC"          -> "zio.sink.v1"
    ),
    Settings.descriptor
  )

  // TODO
  private[this] final val configEnvLayer =
    ZConfig.fromSystemEnv(Settings.descriptor)

  // ZLayer[Any with Console with Clock, ReadError[String], ZConfig[Settings] with Logging with KafkaStreamsTopology with ZConfig[KafkaStreamsRuntime.Service]]
  private[this] final val env =
    ((configLocalLayer ++ Logging.console()) >+> KafkaStreamsTopology.live) >+> KafkaStreamsRuntime.live

  // ZIO[KafkaStreamsRuntime with Logging with ZConfig[Settings], Nothing, Unit]
  private[this] final lazy val program =
    for {
      settings <- config[Settings]
      _        <- log.info(settings.applicationId)
      _        <- KafkaStreamsRuntime.run
    } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program.provideLayer(env).exitCode
}
