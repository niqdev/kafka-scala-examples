package com.kafka.demo

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

import cats.{Monad, Show}
import cats.effect._
import cats.syntax.flatMap.catsSyntaxFlatMapOps
import ciris.ConfigValue
import log.effect.LogWriter
import log.effect.fs2.SyncLogWriter.log4sLog

import scala.concurrent.ExecutionContext

sealed abstract class KafkaStreamsApp[C: Show](config: ConfigValue[C]) extends IOApp.WithContext {

  private[this] def success[F[_]: Monad: LogWriter]: Unit => F[ExitCode] =
    _ => LogWriter.info("Application succeeded") >> Monad[F].pure(ExitCode.Success)

  private[this] def error[F[_]: Monad: LogWriter](e: Throwable): F[ExitCode] =
    LogWriter.error("Application failed", e) >> Monad[F].pure(ExitCode.Error)

  /**
   *
   */
  override def run(args: List[String]): IO[ExitCode] =
    log4sLog[IO](getClass).flatMap(implicit log =>
      KafkaStreamsRuntime[C, IO]
        .run(config)
        .redeemWith(error[IO], success[IO]))

  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] = {
    val acquire = SyncIO(Executors.newCachedThreadPool())
    val release: ExecutorService => SyncIO[Unit] = pool =>
      SyncIO {
        pool.shutdown()
        pool.awaitTermination(10, TimeUnit.SECONDS)
        ()
      }

    Resource
      .make(acquire)(release)
      .map(ExecutionContext.fromExecutorService)
  }
}

/*
 * sbt "cats-kafka-streams/runMain com.kafka.demo.CatsKafkaStreamsApp"
 */
object CatsKafkaStreamsApp extends KafkaStreamsApp(Settings.config)
