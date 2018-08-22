package com.github.demo.http
package route

import java.nio.charset.StandardCharsets.UTF_8
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling
import akka.http.scaladsl.model.MediaTypes.`text/event-stream`
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.model.headers.`Last-Event-ID`
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.{Directives, Route}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{LongDeserializer, StringDeserializer}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

// scalastyle:off underscore.import
import akka.http.scaladsl.server.Directives._
// scalastyle:on underscore.import

final case class Word(value: String, count: Long)

// https://stackoverflow.com/questions/47554394/how-can-i-create-server-side-events-from-an-eventstream-in-akka
// https://stackoverflow.com/questions/51449190/akka-http-first-websocket-client-only-receives-the-data-once-from-a-kafka-topic

trait WordCountRoute {
  this: Routes =>

  private[this] val config = actorSystem.settings.config.getConfig("akka.kafka.consumer")
  private[this] val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new LongDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId(UUID.randomUUID().toString)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  def business(key: String, value: Array[Byte]): Future[Done] = ???

  /**
    * WordCount route.
    *
    * @return Route
    */
  def wordCountRoute: Route =
    path("wordCount") {
      wordCount
    }

  def sse1(size: Int): Route = {
    import Directives._
    import EventStreamMarshalling._

    path("sse1") {
      get {
        optionalHeaderValueByName(`Last-Event-ID`.name) { lastEventId =>
          try {
            val fromSeqNo = lastEventId.map(_.trim.toInt).getOrElse(0) + 1
            complete {
              Source(fromSeqNo.until(fromSeqNo + size))
                .map(n => ServerSentEvent(n.toString))
                .intersperse(ServerSentEvent.heartbeat)
            }
          } catch {
            case _: NumberFormatException =>
              complete(
                HttpResponse(
                  BadRequest,
                  entity = HttpEntity(
                    `text/event-stream`,
                    "Integral number expected for Last-Event-ID header!".getBytes(UTF_8)
                  )
                )
              )
          }
        }
      }
    }
  }

  def sse2: Route = {
    import Directives._
    import EventStreamMarshalling._

    path("sse2") {
      get {
        complete {
          Source
            .tick(2.seconds, 2.seconds, NotUsed)
            .map(_ => ServerSentEvent(DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.now())))
            .keepAlive(1.second, () => ServerSentEvent.heartbeat)
        }
      }
    }
  }

  private[this] def wordCount = {
    import Directives._
    import EventStreamMarshalling._

    pathEndOrSingleSlash {
      get {
        complete {
          Consumer
            .plainSource(consumerSettings, Subscriptions.topics("streams-wordcount-output"))
            .mapAsync(10) { cr =>
              Future {
                Word(cr.key(), cr.value())
              }
            }
            .map(word => ServerSentEvent(s"${word.value}|${word.count}"))
            .keepAlive(1.second, () => ServerSentEvent.heartbeat)
        }
      }
    }
  }

}
