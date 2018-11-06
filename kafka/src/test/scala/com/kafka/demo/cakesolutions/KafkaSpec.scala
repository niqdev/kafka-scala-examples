package com.kafka.demo.cakesolutions

import cakesolutions.kafka.KafkaProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest.{Assertion, Matchers, WordSpecLike}

final class KafkaSpec extends WordSpecLike with Matchers with BaseKafkaSpec {

  private[this] def produce(topic: String,
                            records: Seq[(Option[String], String)]): Unit =
    kafkaServer.produce(topic,
      records.map(record => KafkaProducerRecord(topic, record._1, record._2)),
      new StringSerializer(), new StringSerializer())

  private[this] def verifyConsumer(topic: String, size: Int = 0)
                                  (expectedRecords: => Seq[(Option[String], String)]): Assertion = {
    val records = kafkaServer.consume(
      topic, size, timeoutMills, new StringDeserializer(), new StringDeserializer())

    records.size shouldEqual size
    records shouldBe expectedRecords
  }

  "Kafka" should {

    "produce and consume String from a topic" in {
      val topic = randomString
      val records = Seq(
        (Some("MyKey1"), "MyValue1"),
        (Some("MyKey2"), "MyValue2"),
        (Some("MyKey3"), "MyValue3")
      )

      verifyConsumer(topic) {
        Seq.empty
      }

      produce(topic, records)

      verifyConsumer(topic, records.size) {
        records
      }
    }

  }

}
