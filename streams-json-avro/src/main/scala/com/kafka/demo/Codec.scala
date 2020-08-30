package com.kafka.demo

import java.nio.charset.StandardCharsets

import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import io.circe.{ Decoder, Encoder }
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.Serdes

trait Codec[T] {
  def serde: Serde[T]
}

object Codec extends CodecInstances {

  def apply[T](implicit C: Codec[T]): Codec[T] = C

}

sealed trait CodecInstances {

  implicit val stringCodec: Codec[String] =
    new Codec[String] {
      override def serde: Serde[String] =
        Serdes.String
    }

  private def jsonSerializer[T: Encoder]: T => Array[Byte] =
    data => data.asJson.noSpaces.getBytes(StandardCharsets.UTF_8)

  private def jsonDeserializer[T: Decoder]: Array[Byte] => Option[T] =
    bytes => decode[T](new String(bytes, StandardCharsets.UTF_8)).toOption

  implicit def jsonCodec[T >: Null: Encoder: Decoder]: Codec[T] =
    new Codec[T] {
      override def serde: Serde[T] =
        Serdes.fromFn(jsonSerializer, jsonDeserializer)
    }

}
