package com.kafka.demo

import com.kafka.demo.settings.Settings
import com.kafka.demo.streams.{ KafkaStreamsRuntime, KafkaStreamsTopology }
import zio._
import zio.config._
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

  // option 1: use as layer
  private[this] final val env =
    (Logging.console() ++ configLocalLayer) >+> KafkaStreamsTopology.live // >+> ZLayer.fromManaged(KafkaStreamsRuntime.make)

  private[this] final val program =
    for {
      settings <- getConfig[Settings]
      _ <- log.info(
        s"${generateReport(Settings.descriptor, settings).map(_.toTable.asGithubFlavouredMarkdown)}"
      )
      _ <- log.info(s"${write(Settings.descriptor, settings).map(_.flattenString())}")
      _ <- KafkaStreamsRuntime.make.use(_ => ZIO.succeed()) // option 2
    } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program.provideLayer(env).exitCode
}
