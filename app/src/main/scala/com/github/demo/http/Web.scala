package com.github.demo
package http

import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import com.github.demo.config.AppSettings

import scala.util.{Failure, Success, Try}

trait Web extends Routes {

  def bindAndHandleHttp(onStart: => Unit): Unit = {

    implicit val _ = actorSystem.dispatcher
    val log = Logging(actorSystem, getClass.getName)
    val httpConfig = AppSettings(actorSystem).Http

    Http().bindAndHandle(routes, httpConfig.host, httpConfig.port).onComplete {
      case Success(serverBinding @ ServerBinding(localAddress)) =>
        val (host, port) = (localAddress.getHostName, localAddress.getPort)
        log.info(s"successfully bound to [$host:$port]")
        startApp()
        shutdownHttp(serverBinding)
      case Failure(error) =>
        log.error(error, s"failed to bind to [${httpConfig.host}:${httpConfig.port}]: $error")
        shutdown(failed = true)
    }

    def startApp(): Unit = {
      Try(onStart) match {
        case Success(_) =>
          log.info("successfully started")
        case Failure(error) =>
          log.error(error, s"failed to start: $error")
          shutdown(failed = true)
      }
    }

    def shutdownHttp(serverBinding: ServerBinding): Unit = {
      val _ = sys.addShutdownHook {
        serverBinding.unbind().onComplete {
          case Success(_) =>
            shutdown()
          case Failure(error) =>
            log.error(error, s"failed to shut down: $error")
            shutdown(failed = true)
        }
      }
    }

    def shutdown(failed: Boolean = false): Unit = {
      log.info(s"[failed=$failed] shutting down...")
      materializer.shutdown()
      actorSystem.terminate().onComplete {
        case Success(_) if !failed => sys.exit()
        case _                     => sys.exit(-1)
      }
    }
  }

}
