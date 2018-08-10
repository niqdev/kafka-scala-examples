package com.github.demo
package config

import com.typesafe.config.ConfigFactory

object CommonSettings {
  private[this] lazy val config = ConfigFactory.load()
  private[this] val commonConfig = config getConfig "common"
  private[this] val logConfig = commonConfig getConfig "log"

  val name: String = commonConfig getString "name"
  val logName: String = logConfig getString "name"
}
