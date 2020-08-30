package com.kafka.demo

import zio.config.{ ZConfig, config }
import zio.logging.{ LogLevel, Logging, log }
import zio.{ App, ExitCode, URIO, ZIO }

// sbt "zio-kafka-streams/runMain com.kafka.demo.ZioKafkaStreamsApp"
object ZioKafkaStreamsApp extends App {

  private[this] final type AppEnv = ZConfig[KafkaStreamsConfig] with Logging

  private[this] final lazy val configLayerLocal = ZConfig.fromMap(
    Map(
      "APPLICATION_NAME"    -> "zio-kafka-streams",
      "BOOTSTRAP_SERVERS"   -> "localhost:9092",
      "SCHEMA_REGISTRY_URL" -> "http://localhost:8081"
    ),
    KafkaStreamsConfig.descriptor
  )

  private[this] final lazy val configLayerEnv =
    ZConfig.fromSystemEnv(KafkaStreamsConfig.descriptor)

  private[this] final lazy val loggingLayer =
    Logging.console((_, logEntry) => logEntry)

  final lazy val program: ZIO[AppEnv, Nothing, Unit] =
    for {
      kafkaStreamsConfig <- config[KafkaStreamsConfig]
      _                  <- log(LogLevel.Info)(kafkaStreamsConfig.applicationName)
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program.provideLayer(configLayerLocal ++ loggingLayer).exitCode
}
