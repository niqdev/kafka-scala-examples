package com.kafka.demo

import org.scalatest.{Matchers, WordSpecLike}

final class HelloSpec extends WordSpecLike with Matchers {

  "Hello" should {

    "verify" in {
      true shouldBe true
    }

  }

}
