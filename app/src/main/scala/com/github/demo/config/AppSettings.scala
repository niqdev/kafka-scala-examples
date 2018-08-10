package com.github.demo
package config

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}

import scala.concurrent.duration.FiniteDuration

final class AppSettingsImpl(system: ExtendedActorSystem) extends Extension {
  private[this] lazy val config = system.settings.config
  private[this] val appConfig = config getConfig "app"

  object Http {
    private[this] val httpConfig = appConfig getConfig "http"

    val host: String = httpConfig getString "host"
    val port: Int = httpConfig getInt "port"
    val timeout = FiniteDuration((httpConfig getDuration "timeout").getSeconds, SECONDS)
  }
}

object AppSettings extends ExtensionId[AppSettingsImpl] with ExtensionIdProvider {

  override def lookup: ExtensionId[AppSettingsImpl] = AppSettings

  override def createExtension(system: ExtendedActorSystem): AppSettingsImpl = new AppSettingsImpl(system)

  /**
    * Java API: retrieve the Settings extension for the given system.
    */
  override def get(system: ActorSystem): AppSettingsImpl = super.get(system)
}
