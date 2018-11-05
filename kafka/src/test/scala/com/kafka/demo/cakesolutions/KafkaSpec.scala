package com.kafka.demo.cakesolutions

import cakesolutions.kafka.KafkaProducerRecord
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}
import org.scalatest.{Matchers, WordSpecLike}

final class KafkaSpec extends WordSpecLike with Matchers with BaseKafkaSpec {

  "Kafka" should {

    "produce and consume from a topic" in {
      val key = "MyKey"
      val value = "MyValue"
      val topic = subscribeTopic()

      val records1 = consumer.poll(timeoutMills)
      records1.count() shouldEqual 0

      producer.send(KafkaProducerRecord(topic, Some(key), value))
      producer.flush()

      val records2: ConsumerRecords[String, String] = consumer.poll(timeoutMills)
      records2.count() shouldEqual 1
      records2.iterator().hasNext shouldBe true

      val record: ConsumerRecord[String, String] = records2.iterator().next()
      record.key() shouldBe key
      record.value() shouldBe value
    }

  }

}
