package com.kafka.demo

import java.util.Properties

import cats.Show
import cats.instances.string._
import cats.syntax.parallel._
import cats.syntax.show._
import ciris.refined._
import ciris.{ConfigDecoder, ConfigValue, env}
import com.kafka.demo.streams.LogAndFailProductionExceptionHandler
import eu.timepit.refined.types.all.NonEmptyString
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.estatico.newtype.macros.newtype
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler

object settings {

  final case class Settings(
    applicationId: ApplicationId,
    bootstrapServers: BootstrapServers,
    schemaRegistryUrl: SchemaRegistryUrl,
    sourceTopic: SourceTopic,
    sinkTopic: SinkTopic
  ) {
    // https://docs.confluent.io/current/streams/developer-guide/config-streams.html
    def properties: Properties = {
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId.string.value)
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.string.value)
      props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl.url.value)
      // https://docs.confluent.io/current/streams/faq.html#failure-and-exception-handling
      props.put(
        StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
        classOf[LogAndContinueExceptionHandler]
      )
      props.put(
        StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG,
        classOf[LogAndFailProductionExceptionHandler]
      )
      props
    }
  }

  object Settings {
    implicit val settingsShow: Show[Settings] =
      settings =>
        s"""\n
           |APPLICATION_ID=${settings.applicationId.string.value.show}
           |BOOTSTRAP_SERVERS=${settings.bootstrapServers.string.value.show}
           |SCHEMA_REGISTRY_URL=${settings.schemaRegistryUrl.url.value.show}
           |SOURCE_TOPIC=${settings.sourceTopic.topic.value.show}
           |SINK_TOPIC=${settings.sinkTopic.topic.value.show}
           |""".stripMargin

    lazy val config: ConfigValue[Settings] =
      (
        env("APPLICATION_ID").as[ApplicationId],
        env("BOOTSTRAP_SERVERS").as[BootstrapServers],
        env("SCHEMA_REGISTRY_URL").as[SchemaRegistryUrl],
        env("SOURCE_TOPIC").as[SourceTopic],
        env("SINK_TOPIC").as[SinkTopic]
        ).parMapN(Settings.apply)
  }

  @newtype case class ApplicationId(string: NonEmptyString)
  object ApplicationId {
    final implicit val applicationIdShow: Show[ApplicationId] =
      _.string.value
    final implicit val applicationIdConfigDecoder: ConfigDecoder[String, ApplicationId] =
      ConfigDecoder[String, NonEmptyString].map(ApplicationId.apply)
  }

  @newtype case class BootstrapServers(string: NonEmptyString)
  object BootstrapServers {
    final implicit val bootstrapServersShow: Show[BootstrapServers] =
      _.string.value
    final implicit val bootstrapServersConfigDecoder: ConfigDecoder[String, BootstrapServers] =
      ConfigDecoder[String, NonEmptyString].map(BootstrapServers.apply)
  }

  @newtype case class SchemaRegistryUrl(url: UrlString)
  object SchemaRegistryUrl {
    final implicit val schemaRegistryUrlShow: Show[SchemaRegistryUrl] =
      _.url.value
    final implicit val schemaRegistryUrlConfigDecoder: ConfigDecoder[String, SchemaRegistryUrl] =
      ConfigDecoder[String, UrlString].map(SchemaRegistryUrl.apply)
  }

  @newtype case class SourceTopic(topic: Topic)
  object SourceTopic {
    final implicit val sourceTopicShow: Show[SourceTopic] =
      _.topic.value
    final implicit val sourceTopicConfigDecoder: ConfigDecoder[String, SourceTopic] =
      ConfigDecoder[String, Topic].map(SourceTopic.apply)
  }

  @newtype case class SinkTopic(topic: Topic)
  object SinkTopic {
    final implicit val sinkTopicShow: Show[SinkTopic] =
      _.topic.value
    final implicit val sinkTopicConfigDecoder: ConfigDecoder[String, SinkTopic] =
      ConfigDecoder[String, Topic].map(SinkTopic.apply)
  }
}
