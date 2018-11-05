package com.kafka.demo.cakesolutions

import java.time.{Duration => JDuration}

import cakesolutions.kafka.KafkaProducer
import cakesolutions.kafka.testkit.KafkaServer
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.{KafkaConsumer => JKafkaConsumer}
import org.scalatest.{BeforeAndAfterAll, TestSuite}

import scala.collection.JavaConverters.asJavaCollectionConverter
import scala.util.Random

trait BaseKafkaSpec extends BeforeAndAfterAll {
  this: TestSuite =>

  private[this] val logger = Logger(getClass.getSimpleName)

  private[this] val kafkaServer: KafkaServer = new KafkaServer()
  private[this] val kafkaPort: Int = kafkaServer.kafkaPort
  private[this] val bootstrapServers: String = s"localhost:$kafkaPort"
  private[this] val groupId: String = randomString

  protected[this] val timeoutMills: JDuration =
    JDuration.ofMillis(1000)
  protected[this] val producer: KafkaProducer[String, String] =
    Producer.newProducer(bootstrapServers)
  protected[this] val consumer: JKafkaConsumer[String, String] =
    Consumer.newConsumer(bootstrapServers, groupId)

  private[this] def randomString: String =
    Random.alphanumeric.take(5).mkString("")

  protected[this] def subscribeTopic(): String = {
    val topic = randomString
    consumer.subscribe(List(randomString).asJavaCollection)
    logger.info(s"Using topic [$topic] and kafka port [$kafkaPort]")
    topic
  }

  override def beforeAll(): Unit = {
    kafkaServer.startup()
  }

  override def afterAll(): Unit = {
    producer.close()
    consumer.close()

    kafkaServer.close()
  }

}
