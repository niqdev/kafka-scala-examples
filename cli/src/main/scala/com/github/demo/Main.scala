package com.github.demo

import com.github.demo.cli.CommandLineParser
import com.typesafe.scalalogging.Logger

object Main {
  private[this] val log = Logger(getClass.getName)

  def main(args: Array[String]): Unit = {
    CommandLineParser.parse(args) match {
      case Right(params) =>
        log.debug(s"$params")
      case Left(error) =>
        log.error(s"$error")
    }
  }

}
