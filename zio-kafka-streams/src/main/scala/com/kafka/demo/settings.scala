package com.kafka.demo

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import org.apache.kafka.streams.StreamsConfig
import zio.config.ConfigDescriptor.string

object settings {

  // TODO newtype + refined
  final case class Settings(
    applicationId: String,
    bootstrapServers: String,
    schemaRegistryUrl: String,
    sourceTopic: String,
    sinkTopic: String
  ) {
    // TODO ZIO[x, y, Properties] ?
    def properties: java.util.Properties = {
      val props = new java.util.Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId)
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
      props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
      props
    }
  }

  object Settings {
    val descriptor =
      (string("APPLICATION_NAME") |@|
        string("BOOTSTRAP_SERVERS") |@|
        string("BOOTSTRAP_SERVERS") |@|
        string("SOURCE_TOPIC") |@|
        string("SINK_TOPIC"))(
        Settings.apply,
        Settings.unapply
      )
  }
}
