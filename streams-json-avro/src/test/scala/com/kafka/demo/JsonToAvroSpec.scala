package com.kafka.demo

import net.manub.embeddedkafka.schemaregistry.EmbeddedKafkaConfig
import net.manub.embeddedkafka.schemaregistry.streams.EmbeddedKafkaStreams
import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class JsonToAvroSpec extends AnyWordSpecLike with Matchers with EmbeddedKafkaStreams {

  private[this] val schemaRegistryPort = 7002
  private[this] val schemaRegistryUrl = s"http://localhost:$schemaRegistryPort"
  private[this] val inputTopic = "myInputTopic"
  private[this] val outputTopic = "myOutputTopic"

  private[this] implicit val embeddedKafkaConfig: EmbeddedKafkaConfig =
    EmbeddedKafkaConfig(kafkaPort = 7000,
      zooKeeperPort = 7001,
      schemaRegistryPort = schemaRegistryPort)

  private[this] implicit val stringSerializer: Serializer[String] =
    Codec[String].serde.serializer()

  private[this] implicit val jsonSerializer: Serializer[JsonModel] =
    Codec[JsonModel].serde.serializer()

  implicit private[this] val keyAvroModelDeserializer: Deserializer[KeyAvroModel] =
    AvroCodec[KeyAvroModel].serde(schemaRegistryUrl).deserializer()

  implicit private[this] val valueAvroModelDeserializer: Deserializer[ValueAvroModel] =
    AvroCodec[ValueAvroModel].serde(schemaRegistryUrl).deserializer()

  "JsonToAvro" should {

    "verify topology" in {
      val messageKey = "myKeyExample"
      val messageValue = "{\"valueInt\":\"8\",\"valueString\":\"myStringExample\"}"
      val topology = JsonToAvroApp.buildTopology(schemaRegistryUrl, inputTopic, outputTopic)

      runStreams(Seq(inputTopic, outputTopic), topology) {

        publishToKafka(inputTopic, messageKey, messageValue)

        val (resultKey, resultValue) = consumeFirstKeyedMessageFrom(outputTopic)(
          embeddedKafkaConfig, keyAvroModelDeserializer, valueAvroModelDeserializer)

        resultKey shouldBe KeyAvroModel(messageKey)
        resultValue shouldBe ValueAvroModel(8, "MYSTRINGEXAMPLE")
      }
    }

  }
}
