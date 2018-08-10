package com.github.demo
package config

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{Matchers, WordSpecLike}

final class AppSettingsSpec extends TestKit(ActorSystem("settings-test")) with WordSpecLike with Matchers {

  val appSettings = AppSettings(system)

  object ConfigTest {
    private[this] val appConfig = system.settings.config getConfig "app"
    private[this] val dockerConfig = appConfig getConfig "docker"

    val dockerPort: Int = dockerConfig getInt "port"
  }

  "exposed docker port" should {
    "be equal to app http port" in {
      ConfigTest.dockerPort shouldEqual appSettings.Http.port
    }
  }

}
