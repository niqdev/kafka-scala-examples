package com.kafka.demo

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class KafkaSchemaRegistryGenericSpec
    extends AnyWordSpecLike
    with Matchers
    with BaseKafkaSchemaRegistrySpec {

  private[this] val SCHEMA_CUSTOMER_V1 = "/customer_v1.avsc"
  private[this] val SCHEMA_CUSTOMER_V2 = "/customer_v2.avsc"
  private[this] val schemaCustomerV1 =
    new Schema.Parser().parse(getClass.getResourceAsStream(SCHEMA_CUSTOMER_V1))
  private[this] val schemaCustomerV2 =
    new Schema.Parser().parse(getClass.getResourceAsStream(SCHEMA_CUSTOMER_V2))

  private[this] def verifyGenericConsumer(
    topic: String
  )(expectedRecords: => Iterable[(Option[AnyRef], AnyRef)]): Assertion = {
    val records = consume(topic, expectedRecords.size, isSpecific = false)
    records.size shouldEqual expectedRecords.size
    records shouldBe expectedRecords
  }

  "KafkaGeneric" should {

    "produce and consume GenericRecord" in {
      val topic = randomString

      val records: Iterable[(Option[AnyRef], AnyRef)] =
        (1 to 10)
          .map { i =>
            val customer = new GenericData.Record(schemaCustomerV1)
            customer.put("name", s"name-$i")
            val key = s"id-$i"
            new ProducerRecord[AnyRef, AnyRef](topic, key, customer)
            (Some(key), customer)
          }

      verifyGenericConsumer(topic) {
        Iterable.empty
      }

      produce(topic, records)

      verifyGenericConsumer(topic) {
        records
      }
    }

    // TODO
    "produce v1 and consume v2" in {
      true shouldBe true
    }

  }

}
