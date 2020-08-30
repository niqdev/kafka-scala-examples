package com.kafka.demo

import java.util.concurrent.{ ExecutorService, Executors, TimeUnit }

import cats.Monad
import cats.effect._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.show._
import com.kafka.demo.settings.Settings
import com.kafka.demo.streams.KafkaStreamsRuntime
import log.effect.LogWriter
import log.effect.fs2.SyncLogWriter.log4sLog

import scala.concurrent.ExecutionContext

// sbt "cats-kafka-streams/runMain com.kafka.demo.CatsKafkaStreamsApp"
object CatsKafkaStreamsApp extends IOApp.WithContext {

  /**
    * Run Kafka Streams application
    */
  override def run(args: List[String]): IO[ExitCode] =
    log4sLog[IO](getClass)
      .flatMap(implicit logger => app[IO].redeemWith(onError[IO], onSuccess[IO]))

  private[this] final def onSuccess[F[_]: Monad: LogWriter]: Unit => F[ExitCode] =
    _ => LogWriter.info("Application succeeded") >> Monad[F].pure(ExitCode.Success)

  private[this] final def onError[F[_]: Monad: LogWriter](e: Throwable): F[ExitCode] =
    LogWriter.error("Application failed", e) >> Monad[F].pure(ExitCode.Error)

  private[this] final def app[F[_]: Async: ContextShift: Timer: LogWriter]: F[Unit] =
    for {
      settings <- Settings.config.load[F]
      _        <- LogWriter.info("Load settings ...")
      _        <- LogWriter.info(settings.show)
      _        <- LogWriter.info("Start application ...")
      _        <- KafkaStreamsRuntime[F].run(settings)
    } yield ()

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
