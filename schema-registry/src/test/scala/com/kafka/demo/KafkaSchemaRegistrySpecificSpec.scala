package com.kafka.demo

import io.confluent.examples.clients.basicavro.Payment
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class KafkaSchemaRegistrySpecificSpec
    extends AnyWordSpecLike
    with Matchers
    with BaseKafkaSchemaRegistrySpec {

  private[this] def verifySpecificConsumer(
    topic: String
  )(expectedRecords: => Iterable[(Option[AnyRef], AnyRef)]): Assertion = {
    val records = consume(topic, expectedRecords.size, isSpecific = true)
    records.size shouldEqual expectedRecords.size
    records shouldBe expectedRecords
  }

  "KafkaSpecific" should {

    "produce and consume SpecificRecord" in {
      val topic = randomString

      val records: Iterable[(Option[String], Payment)] =
        (1 to 10)
          .map { i =>
            val orderId = s"id-$i"
            val payment = Payment(orderId, 100 + i)
            (Some(orderId), payment)
          }

      verifySpecificConsumer(topic) {
        Iterable.empty
      }

      produce(topic, records)

      verifySpecificConsumer(topic) {
        records
      }
    }

  }

}
