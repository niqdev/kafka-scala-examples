package com.github.demo
package http

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.github.demo.http.route.{EnvRoute, MetricsRoute, StatusRoute, WordCountRoute}

import scala.concurrent.ExecutionContext

// scalastyle:off underscore.import
import akka.http.scaladsl.server.Directives._
// scalastyle:on underscore.import

trait Routes extends StatusRoute with MetricsRoute with EnvRoute with WordCountRoute {

  protected[this] implicit def actorSystem: ActorSystem
  protected[this] implicit def materializer: ActorMaterializer
  protected[this] implicit def executionContext: ExecutionContext
  protected[this] implicit def timeout: Timeout

  /**
    * All routes.
    *
    * @return Route
    */
  def routes: Route =
    statusRoute ~ metricsRoute ~ envRoute ~
      wordCountRoute ~ sse1(10) ~ sse2

}
