package com.kafka.demo

import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }

final case class JsonModel(valueInt: Int, valueString: String)

object JsonModel {
  implicit val jsonModelDecoder: Decoder[JsonModel] = deriveDecoder[JsonModel]
  implicit val jsonModelEncoder: Encoder[JsonModel] = deriveEncoder[JsonModel]
}

final case class KeyAvroModel(myKey: String)
final case class ValueAvroModel(myInt: Int, myString: String)
