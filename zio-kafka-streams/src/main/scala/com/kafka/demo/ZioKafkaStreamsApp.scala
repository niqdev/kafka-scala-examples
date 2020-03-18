package com.kafka.demo

import zio.config.{Config, config}
import zio.logging.{Logging, log}
import zio.{App, UIO, ZEnv, ZIO}

object ZioKafkaStreamsApp extends App {

  private[this] final type AppEnv = Config[KafkaStreamsConfig] with Logging

  private[this] final lazy val configLayer = Config.fromMap(Map(
    "APPLICATION_NAME" -> "zio-kafka-streams",
    "BOOTSTRAP_SERVERS" -> "localhost:9092",
    "SCHEMA_REGISTRY_URL" -> "http://localhost:8081"
  ), KafkaStreamsConfig.config)

  private[this] final lazy val loggingLayer =
    Logging.console((_, logEntry) => logEntry)

  final lazy val program: ZIO[AppEnv, Nothing, Unit] =
    for {
      kafkaStreamsConfig <- config[KafkaStreamsConfig]
      _ <- log(kafkaStreamsConfig.applicationName)
    } yield ()

  /*
   * sbt "zio-kafka-streams/runMain com.kafka.demo.ZioKafkaStreamsApp"
   */
  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    program
      .provideLayer(configLayer ++ loggingLayer)
      .foldM(error => zio.console.putStrLn(s"ERROR: $error") *> UIO.succeed(1), _ => UIO.succeed(0))
}
