package com.kafka.demo.streams

import cats.effect.{Async, Resource, Sync, Timer}
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.kafka.demo.settings.Settings
import log.effect.LogWriter
import org.apache.kafka.streams.KafkaStreams
import retry.RetryDetails.{GivingUp, WillDelayAndRetry}
import retry.syntax.all._
import retry.{RetryDetails, RetryPolicies}

import scala.concurrent.duration._

// https://docs.confluent.io/current/streams/developer-guide/write-streams.html
// https://github.com/confluentinc/kafka-streams-examples
sealed abstract class KafkaStreamsRuntime[F[_] : Async : Timer](implicit log: LogWriter[F]) {

  def run(settings: Settings): F[Unit] =
    Resource.make[F, KafkaStreams](setup(settings))(stop()).use(start)

  private[this] def setup: Settings => F[KafkaStreams] =
    settings =>
      for {
        _ <- log.info("Build topology ...")
        topology <- KafkaStreamsTopology[F].build(settings)
        streams <- Sync[F].delay(new KafkaStreams(topology, settings.properties))
      } yield streams

  private[this] def start: KafkaStreams => F[Unit] =
    kafkaStreams =>
      Async[F].asyncF { callback =>

        def setupShutdownHandler: F[Unit] =
          Sync[F].delay {
            kafkaStreams
              .setUncaughtExceptionHandler((_: Thread, throwable: Throwable) => callback(Left(throwable)))

            kafkaStreams
              .setStateListener((newState: KafkaStreams.State, _: KafkaStreams.State) =>
                newState match {
                  case KafkaStreams.State.ERROR =>
                    callback(Left(new IllegalStateException("Shut down application in ERROR state")))
                  case KafkaStreams.State.NOT_RUNNING =>
                    callback(Right(()))
                  case _ => ()
                }
              )
          }

        // to gracefully shutdown in response to SIGTERM
        def setupGracefulShutdown: F[Unit] =
          Sync[F].delay {
            Runtime.getRuntime.addShutdownHook(new Thread(() => kafkaStreams.close()))
          }

        // start forever: callback(Right) shouldn't be invoked after the application starts successfully
        log.info("Start streams ...") >>
          setupShutdownHandler >>
          Sync[F].delay(kafkaStreams.start()) >>
          setupGracefulShutdown
      }

  private[this] def stop(maxAttempts: Int = 5): KafkaStreams => F[Unit] =
    kafkaStreams => {

      lazy val retryWithDelayPolicy =
        RetryPolicies.limitRetries[F](maxAttempts) join RetryPolicies.constantDelay[F](500.milliseconds)

      def onError(error: Throwable, details: RetryDetails): F[Unit] =
        details match {
          case WillDelayAndRetry(
          nextDelay: FiniteDuration,
          retriesSoFar: Int,
          cumulativeDelay: FiniteDuration
          ) =>
            log.warn(
              s"Stop streams: attempt [$retriesSoFar] of [$maxAttempts], retry in [$nextDelay] after [$cumulativeDelay]",
              error
            )
          case GivingUp(totalRetries: Int, totalDelay: FiniteDuration) =>
            log.error(
              s"Stop streams: max attempt exceeded [$totalRetries], give up after [$totalDelay]",
              error
            )
        }

      // close(Duration) throws exception and trigger retry
      log.info("Stop streams ...") >>
        Sync[F].delay(kafkaStreams.close(java.time.Duration.ofSeconds(1))) >>
        Sync[F].unit.retryingOnAllErrors(retryWithDelayPolicy, onError)
    }
}

object KafkaStreamsRuntime {
  def apply[F[_] : Async : LogWriter : Timer]: KafkaStreamsRuntime[F] =
    new KafkaStreamsRuntime {}
}
