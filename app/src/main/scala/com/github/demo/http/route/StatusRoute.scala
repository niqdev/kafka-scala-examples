package com.github.demo.http
package route

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path, pathEndOrSingleSlash}
import akka.http.scaladsl.server.Route

// scalastyle:off underscore.import
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import io.circe.syntax._
// scalastyle:on underscore.import

final case class Status(value: String)

trait StatusRoute {
  this: Routes =>

  /**
    * Status route.
    *
    * @return Route
    */
  def statusRoute: Route =
    path("status") {
      status
    }

  private[this] def status =
    pathEndOrSingleSlash {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, Status("OK").asJson.noSpaces))
      }
    }

}
