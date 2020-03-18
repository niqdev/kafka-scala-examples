package com.kafka.demo

import zio.config.ConfigDescriptor
import zio.config.ConfigDescriptor.string

// TODO nested
final case class AppConfig(
  kafkaStreams: KafkaStreamsConfig
)

// TODO newtype + refined
final case class KafkaStreamsConfig(
  applicationName: String,
  bootstrapServers: String,
  schemaRegistryUrl: String
)

object KafkaStreamsConfig {
  val config: ConfigDescriptor[String, String, KafkaStreamsConfig] =
    (string("APPLICATION_NAME") |@|
      string("BOOTSTRAP_SERVERS") |@|
      string("SCHEMA_REGISTRY_URL")) (
      KafkaStreamsConfig.apply,
      KafkaStreamsConfig.unapply
    )
}
