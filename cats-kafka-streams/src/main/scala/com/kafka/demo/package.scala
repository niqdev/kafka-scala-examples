package com.kafka

import eu.timepit.refined.types.string.NonEmptyString

package object demo {

  // TODO newtype + refined
  final type ApplicationName = NonEmptyString
  final type BootstrapServers = NonEmptyString
  final type SchemaRegistry = NonEmptyString
  final type InputTopic = NonEmptyString
  final type OutputTopic = NonEmptyString
}
