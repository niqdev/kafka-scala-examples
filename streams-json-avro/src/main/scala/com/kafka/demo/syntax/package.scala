package com.kafka.demo

import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

package object syntax {

  implicit final def streamsBuilderSyntax(builder: StreamsBuilder): StreamsBuilderOps =
    new StreamsBuilderOps(builder)

  implicit final def kStreamSyntax[K, V](kStream: KStream[K, V]): KStreamOps[K, V] =
    new KStreamOps(kStream)

}
