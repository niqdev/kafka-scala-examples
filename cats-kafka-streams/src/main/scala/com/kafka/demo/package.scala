package com.kafka

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Url
import eu.timepit.refined.types.string.NonEmptyString

package object demo {

  final type UrlString = String Refined Url
  final type Topic     = NonEmptyString
}
