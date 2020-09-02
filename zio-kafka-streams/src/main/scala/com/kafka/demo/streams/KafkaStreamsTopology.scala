package com.kafka.demo
package streams

import com.kafka.demo.settings.Settings
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.scala.StreamsBuilder
import zio._
import zio.logging.Logger

// TODO ZKStream and ZKTable
object KafkaStreamsTopology {
  type KafkaStreamsTopology = Has[KafkaStreamsTopology.Service]

  trait Service {
    def build: Task[Topology]
  }
  object Service {
    val live: (Logger[String], Settings) => Service =
      (log, settings) =>
        new Service {
          override def build: Task[Topology] =
            for {
              _ <- log.info("Build topology ...")
              topology <- ZIO.effect {
                import org.apache.kafka.streams.scala.ImplicitConversions.{
                  consumedFromSerde,
                  producedFromSerde
                }
                import org.apache.kafka.streams.scala.Serdes.String

                val builder = new StreamsBuilder()

                // simplest topology possible: no avro/schema-registry
                val sourceStream    = builder.stream[String, String](settings.sourceTopic)(consumedFromSerde)
                val upperCaseStream = sourceStream.mapValues(_.toUpperCase())
                upperCaseStream.to(settings.sinkTopic)(producedFromSerde)

                builder.build()
              }
            } yield topology
        }
  }

  // ??? ZLayer[Logging with ZConfig[Settings], Nothing, KafkaStreamsTopology]
  val live =
    ZLayer.fromServices(Service.live)

  def build: RIO[KafkaStreamsTopology, Topology] =
    ZIO.accessM[KafkaStreamsTopology](_.get.build)

}
