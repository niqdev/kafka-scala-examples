package com.kafka.demo

import cats.Show
import cats.instances.string.catsStdShowForString
import cats.syntax.parallel.catsSyntaxTuple5Parallel
import cats.syntax.show.toShow
import ciris.refined.refTypeConfigDecoder
import ciris.{ConfigValue, env}

final case class Settings(
  applicationName: ApplicationName,
  bootstrapServers: BootstrapServers,
  schemaRegistry: SchemaRegistry,
  inputTopic: InputTopics,
  outputTopic: OutputTopic
)

sealed trait SettingsInstances {

  implicit val settingsShow: Show[Settings] =
    settings =>
      s"""
         | APPLICATION_NAME: ${settings.applicationName.value.show}
         | BOOTSTRAP_SERVERS: ${settings.bootstrapServers.value.show}
         | SCHEMA_REGISTRY: ${settings.schemaRegistry.value.show}
         | INPUT_TOPIC: ${settings.inputTopic.value.show}
         | OUTPUT_TOPIC: ${settings.outputTopic.value.show}
      """.stripMargin
}

object Settings extends SettingsInstances {

  final def config: ConfigValue[Settings] =
    (
      env("APPLICATION_NAME").as[ApplicationName],
      env("BOOTSTRAP_SERVERS").as[BootstrapServers],
      env("SCHEMA_REGISTRY").as[SchemaRegistry],
      env("INPUT_TOPIC").as[InputTopics],
      env("OUTPUT_TOPIC").as[OutputTopic]
    ).parMapN(Settings.apply)
}
