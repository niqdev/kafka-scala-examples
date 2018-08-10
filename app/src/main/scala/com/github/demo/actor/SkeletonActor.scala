package com.github.demo
package actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import com.github.demo.actor.SkeletonActor.{RequestMessage, ResponseMessage}

object SkeletonActor {
  def name: String = "skeleton-actor"
  def props: Props = Props[SkeletonActor]

  sealed trait Message
  case class RequestMessage(value: String) extends Message
  case object ResponseMessage extends Message
}

final class SkeletonActor extends Actor with ActorLogging {
  override def receive: Receive = LoggingReceive {
    case RequestMessage(value) =>
      log.debug(s"message: $value")
      sender() ! ResponseMessage
    case _ =>
      log.error("invalid message")
  }
}
