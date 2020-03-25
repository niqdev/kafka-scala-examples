package com.kafka.demo

import cats.Show
import cats.effect.{Async, ContextShift}
import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import cats.syntax.show.toShow
import ciris.ConfigValue
import log.effect.LogWriter

sealed abstract class KafkaStreamsRuntime[C: Show, F[_] : Async : ContextShift : LogWriter] {

  // TODO
  def run(config: ConfigValue[C]): F[Unit] =
    for {
      settings <- config.load[F]
      _ <- LogWriter.info(settings.show)
    } yield ()

}

object KafkaStreamsRuntime {
  def apply[C: Show, F[_] : Async : ContextShift : LogWriter]: KafkaStreamsRuntime[C, F] =
    new KafkaStreamsRuntime {}
}
