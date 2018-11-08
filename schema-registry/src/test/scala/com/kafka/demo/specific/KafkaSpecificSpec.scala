package com.kafka.demo.specific

import com.kafka.demo.BaseKafkaSpec
import io.confluent.examples.clients.basicavro.Payment
import org.scalatest.{Assertion, Matchers, WordSpecLike}

final class KafkaSpecificSpec extends WordSpecLike with Matchers with BaseKafkaSpec {

  private[this] def verifyConsumer(topic: String)
                                  (expectedRecords: => Iterable[(AnyRef, AnyRef)]): Assertion = {
    val records = consume(topic, expectedRecords.size)
    records.size shouldEqual expectedRecords.size
    records shouldBe expectedRecords
  }

  "KafkaSpecific" should {

    "produce and consume Payment" in {
      val topic = randomString

      val records: Iterable[(String, Payment)] = (1 to 10)
        .map { i =>
          val orderId = s"id-$i"
          val payment = Payment(orderId, 100 + i)
          (orderId, payment)
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
