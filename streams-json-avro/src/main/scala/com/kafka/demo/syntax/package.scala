package com.kafka.demo

import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

package object syntax {

  final implicit def streamsBuilderSyntax(builder: StreamsBuilder): StreamsBuilderOps =
    new StreamsBuilderOps(builder)

  final implicit def kStreamSyntax[K, V](kStream: KStream[K, V]): KStreamOps[K, V] =
    new KStreamOps(kStream)

}
