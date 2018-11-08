package com.kafka.demo.cakesolutions

import cakesolutions.kafka.KafkaProducerRecord
import cakesolutions.kafka.testkit.KafkaServer
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest.{Assertion, BeforeAndAfterAll, Matchers, WordSpecLike}

final class KafkaSpec extends WordSpecLike with Matchers with BeforeAndAfterAll {

  private[this] val kafkaServer: KafkaServer = new KafkaServer()

  override def beforeAll(): Unit =
    kafkaServer.startup()

  override def afterAll(): Unit =
    kafkaServer.close()

  private[this] def produce(topic: String,
                            records: Seq[(Option[String], String)]): Unit =
    kafkaServer.produce(
      topic,
      records.map(record => KafkaProducerRecord(topic, record._1, record._2)),
      new StringSerializer(),
      new StringSerializer())

  private[this] def consume(topic: String,
                            size: Int = 0,
                            timeoutMills: Long = 1000): Seq[(Option[String], String)] =
    kafkaServer.consume(
      topic,
      size,
      timeoutMills,
      new StringDeserializer(),
      new StringDeserializer())

  private[this] def verifyConsumer(topic: String)
                                  (expectedRecords: => Seq[(Option[String], String)]): Assertion = {
    val records = consume(topic, expectedRecords.size)
    records.size shouldEqual expectedRecords.size
    records shouldBe expectedRecords
  }

  "Kafka" should {

    "produce and consume String from a topic" in {
      val topic = "test-topic"
      val records = Seq(
        (Some("MyKey1"), "MyValue1"),
        (Some("MyKey2"), "MyValue2"),
        (Some("MyKey3"), "MyValue3")
      )

      verifyConsumer(topic) {
        Seq.empty
      }

      produce(topic, records)

      verifyConsumer(topic) {
        records
      }
    }

  }

}
