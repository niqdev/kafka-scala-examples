package com.kafka.demo

import com.sksamuel.avro4s.RecordFormat
import net.manub.embeddedkafka.schemaregistry.EmbeddedKafkaConfig
import net.manub.embeddedkafka.schemaregistry.streams.EmbeddedKafkaStreamsAllInOne
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import org.scalatest.{Matchers, WordSpecLike}

final class JsonToAvroSpec extends WordSpecLike with Matchers with EmbeddedKafkaStreamsAllInOne {

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

  private[this] implicit val keyGenericRecordDeserializer: Deserializer[GenericRecord] =
    AvroRecord.genericAvroSerde(schemaRegistryUrl, isKey = true).deserializer()

  private[this] implicit val valueGenericRecordDeserializer: Deserializer[GenericRecord] =
    AvroRecord.genericAvroSerde(schemaRegistryUrl).deserializer()

  "JsonToAvro" should {

    "verify topology" in {
      val messageKey = "myKeyExample"
      val messageValue = JsonModel(8, "myString")
      val topology = JsonToAvroApp.buildTopology(schemaRegistryUrl, inputTopic, outputTopic)

      val expectedKey = KeyAvroModel(messageKey)
      val expectedValue = ValueAvroModel(messageValue.myInt, messageValue.myString.toUpperCase)

      runStreams(Seq(inputTopic, outputTopic), topology) {

        publishToKafka(inputTopic, messageKey, messageValue)

        val (resultKey, resultValue) = consumeFirstKeyedMessageFrom(outputTopic)(
          embeddedKafkaConfig, keyGenericRecordDeserializer, valueGenericRecordDeserializer)

        RecordFormat[KeyAvroModel].from(resultKey) shouldBe expectedKey
        RecordFormat[ValueAvroModel].from(resultValue) shouldBe expectedValue
      }
    }

  }
}
