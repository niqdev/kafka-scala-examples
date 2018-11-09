package com.kafka.demo

import io.confluent.examples.clients.basicavro.Payment
import org.scalatest.{Assertion, Matchers, WordSpecLike}

final class KafkaSchemaRegistrySpecificSpec extends WordSpecLike with Matchers with BaseKafkaSchemaRegistrySpec {

  private[this] def verifyConsumer(topic: String)
                                  (expectedRecords: => Iterable[(Option[AnyRef], AnyRef)]): Assertion = {
    val records = consume(topic, expectedRecords.size)
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
