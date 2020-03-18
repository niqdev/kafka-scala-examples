package com.kafka.demo
package topology

import com.kafka.demo.KafkaStreamsConfig
import org.apache.kafka.streams.Topology
import zio.{Has, RIO}

trait KafkaStreamsTopology {
  def build: KafkaStreamsTopology.Service
}

object KafkaStreamsTopology {
  type KafkaStreamsTopology = Has[KafkaStreamsTopology.Service]

  trait Service {
    def build: RIO[KafkaStreamsConfig, Topology]
  }

  trait Live extends KafkaStreamsTopology.Service {
    def build: RIO[KafkaStreamsConfig, Topology] = ???
  }
}
