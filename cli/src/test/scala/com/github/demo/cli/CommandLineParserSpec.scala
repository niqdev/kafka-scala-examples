package com.github.demo
package cli

import org.scalatest.{Matchers, WordSpecLike}

final class CommandLineParserSpec extends WordSpecLike with Matchers {

  "CommandLineParser" must {

    "parse missing parameters" in {
      val args = Seq.empty[String]

      CommandLineParser.parse(args) match {
        case Left(error) =>
          error should equal("invalid command line arguments")
        case _ =>
          throw new Exception("should fail")
      }
    }

    "parse valid parameters" in {
      val args = Seq("--player", "MyName", "--game", "Rock")
      CommandLineParser.parse(args) match {
        case Right(params) =>
          params should equal(Params("MyName", Game.Rock, None))
        case _ =>
          throw new Exception("should fail")
      }
    }

  }

  "parseAge" must {

    "verify valid age" in {
      CommandLineParser.parseAge(0) should equal(Some(0))
      CommandLineParser.parseAge(100) should equal(Some(100))
    }

    "verify invalid age" in {
      CommandLineParser.parseAge(-1) should equal(None)
    }

  }

}
