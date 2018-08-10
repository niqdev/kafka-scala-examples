package com.github.demo.http
package route

import java.io.StringWriter

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{complete, get, path, pathEndOrSingleSlash}
import akka.http.scaladsl.server.Route
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

// scalastyle:off underscore.import
import akka.http.scaladsl.server.Directives._
// scalastyle:on underscore.import

trait MetricsRoute {
  this: Routes =>

  private[this] val `text/plain; version=0.0.4; charset=utf-8` = ContentType {
    MediaType.customWithFixedCharset(
      "text",
      "plain",
      HttpCharsets.`UTF-8`,
      params = Map("version" -> "0.0.4")
    )
  }

  /**
    * Metrics route.
    *
    * @return Route
    */
  def metricsRoute: Route =
    path("metrics") {
      metrics
    }

  private[this] def renderMetrics = {
    val writer = new StringWriter()
    TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples())
    writer.toString
  }

  private[this] def metrics =
    pathEndOrSingleSlash {
      get {
        complete(HttpEntity(`text/plain; version=0.0.4; charset=utf-8`, renderMetrics))
      }
    }

}
