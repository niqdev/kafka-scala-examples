package com.kafka.demo

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import org.apache.kafka.streams.StreamsConfig
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
) {
  def properties: java.util.Properties = {
    val props = new java.util.Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationName)
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
    props
  }
}

object KafkaStreamsConfig {
  val descriptor =
    (string("APPLICATION_NAME") |@|
      string("BOOTSTRAP_SERVERS") |@|
      string("SCHEMA_REGISTRY_URL")) (
      KafkaStreamsConfig.apply,
      KafkaStreamsConfig.unapply
    )
}
