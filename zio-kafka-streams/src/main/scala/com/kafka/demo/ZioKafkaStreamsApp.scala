package com.kafka.demo

import zio.config.{Config, config}
import zio.console.{Console, putStrLn}
import zio.{App, UIO, ZEnv, ZIO}

// sbt "zio-kafka-streams/runMain com.kafka.demo.ZioKafkaStreamsApp"
object ZioKafkaStreamsApp extends App {

  private[this] lazy val configLayer = Config.fromMap(Map(
    "APPLICATION_NAME" -> "zio-kafka-streams",
    "BOOTSTRAP_SERVERS" -> "localhost:9092",
    "SCHEMA_REGISTRY_URL" -> "http://localhost:8081"
  ), KafkaStreamsConfig.config)

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    program
      .provideLayer(configLayer ++ Console.live)
      .foldM(error => putStrLn(s"ERROR: $error") *> UIO.succeed(1), _ => UIO.succeed(0))

  final lazy val program: ZIO[Config[KafkaStreamsConfig] with Console, Nothing, Unit] =
    for {
      kafkaStreamsConfig <- config[KafkaStreamsConfig]
      _ <- putStrLn(kafkaStreamsConfig.applicationName)
    } yield ()
}
