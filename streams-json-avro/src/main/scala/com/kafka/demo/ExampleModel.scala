package com.kafka.demo

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class JsonModel(myInt: Int, myString: String)

object JsonModel {
  implicit val fooDecoder: Decoder[JsonModel] = deriveDecoder[JsonModel]
  implicit val fooEncoder: Encoder[JsonModel] = deriveEncoder[JsonModel]
}

final case class KeyAvroModel(myKey: String)
final case class ValueAvroModel(myInt: Int, myString: String)
