package com.kafka.demo

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ExampleModel(myInt: Int, myString: String)

object ExampleModel {

  implicit val fooDecoder: Decoder[ExampleModel] = deriveDecoder[ExampleModel]
  implicit val fooEncoder: Encoder[ExampleModel] = deriveEncoder[ExampleModel]

}
