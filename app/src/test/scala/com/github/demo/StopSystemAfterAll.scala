package com.github.demo

import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Suite}

// stop test actor system after all tests are finished
trait StopSystemAfterAll extends BeforeAndAfterAll {

  // ensure that this trait can only be used within a test that extends TestKit
  this: TestKit with Suite =>

  override protected def afterAll(): Unit = {
    super.afterAll()
    val _ = system.terminate()
  }
}
