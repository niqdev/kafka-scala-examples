package com.kafka.demo

import com.typesafe.scalalogging.Logger

object Main extends App {
  private[this] val logger = Logger(getClass.getSimpleName)

  logger.debug("hello")
}
