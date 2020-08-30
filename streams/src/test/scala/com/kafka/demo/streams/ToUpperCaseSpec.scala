package com.kafka.demo
package streams

import org.apache.kafka.common.serialization.{ StringDeserializer, StringSerializer }
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

/*
 * https://kafka.apache.org/11/documentation/streams/developer-guide/testing.html
 * https://github.com/bbejeck/kafka-streams-in-action/blob/master/src/test/java/bbejeck/chapter_3/KafkaStreamsYellingIntegrationTest.java
 * https://github.com/bbejeck/kafka-streams-in-action/blob/master/src/test/java/bbejeck/chapter_8/StockPerformanceStreamsProcessorTopologyTest.java
 */
final class ToUpperCaseSpec extends AnyWordSpecLike with Matchers {

  private[this] val topologyTestDriver: TopologyTestDriver =
    new TopologyTestDriver(ToUpperCaseApp.topology, ToUpperCaseApp.properties)

  "ToUpperCase" should {

    "verify topology" in {
      val value         = "mY lower CAse valuE"
      val expectedValue = "MY LOWER CASE VALUE"

      val consumerRecordFactory = new ConsumerRecordFactory[String, String](
        ToUpperCaseApp.IN_TOPIC,
        new StringSerializer,
        new StringSerializer
      )
      topologyTestDriver.pipeInput(consumerRecordFactory.create(value))
      val producerRecord = topologyTestDriver.readOutput(
        ToUpperCaseApp.OUT_TOPIC,
        new StringDeserializer,
        new StringDeserializer
      )

      producerRecord.key() shouldBe null
      producerRecord.value() shouldBe expectedValue
    }
  }

}
