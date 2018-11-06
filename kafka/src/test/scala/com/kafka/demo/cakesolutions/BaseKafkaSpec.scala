package com.kafka.demo.cakesolutions

import cakesolutions.kafka.testkit.KafkaServer
import org.scalatest.{BeforeAndAfterAll, TestSuite}

import scala.util.Random

trait BaseKafkaSpec extends BeforeAndAfterAll {
  this: TestSuite =>

  protected[this] val kafkaServer: KafkaServer = new KafkaServer()
  protected[this] val timeoutMills: Long = 1000

  protected[this] def randomString: String =
    Random.alphanumeric.take(5).mkString("")

  override def beforeAll(): Unit =
    kafkaServer.startup()

  override def afterAll(): Unit =
    kafkaServer.close()

}
