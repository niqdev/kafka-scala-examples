package com.github.demo.http
package route

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path, pathEndOrSingleSlash}
import akka.http.scaladsl.server.Route

// scalastyle:off underscore.import
import akka.http.scaladsl.server.Directives._
import io.circe.syntax._
import scala.collection.JavaConverters._
// scalastyle:on underscore.import

trait EnvRoute {
  this: Routes =>

  /**
    * Environment variables route.
    *
    * @return Route
    */
  def envRoute: Route =
    path("env") {
      env
    }

  private[this] def env =
    pathEndOrSingleSlash {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, System.getenv().asScala.asJson.noSpaces))
      }
    }

}
