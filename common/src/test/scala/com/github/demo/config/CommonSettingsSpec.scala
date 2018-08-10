package com.github.demo
package config

import org.scalatest.{Matchers, WordSpecLike}

final class CommonSettingsSpec extends WordSpecLike with Matchers {

  "name" should {
    "be equal to kafka-stream-demo" in {
      CommonSettings.name shouldEqual "kafka-stream-demo"
    }
  }

  "log file name" should {
    "be equal to name" in {
      CommonSettings.logName shouldEqual CommonSettings.name
    }
  }

}
