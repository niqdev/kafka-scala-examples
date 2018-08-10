package com.github.demo

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer
import akka.util.Timeout
import io.prometheus.client.hotspot.DefaultExports
import com.github.demo.config.AppSettings
import com.github.demo.http.Web

import scala.concurrent.ExecutionContext

object Server extends Web with App {

  protected[this] implicit val actorSystem: ActorSystem = ActorSystem("kafka-stream-demo")
  protected[this] implicit val materializer: ActorMaterializer = ActorMaterializer()
  protected[this] implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  private[this] lazy val httpConfig = AppSettings(actorSystem).Http
  protected[this] implicit val timeout: Timeout = httpConfig.timeout

  private[this] val log = Logging(actorSystem, getClass.getName)

  // JVM metrics from MBeans
  DefaultExports.initialize()

  bindAndHandleHttp {
    log.debug("run server")
    WordCountScalaExample.run
  }

}
