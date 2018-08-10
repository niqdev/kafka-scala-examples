package com.github.demo
package actor

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.WordSpecLike
import com.github.demo.actor.SkeletonActor.RequestMessage

object SkeletonActorLogSpec {
  val testSystem: ActorSystem = {
    val config = ConfigFactory.parseString("""
      akka.loglevel = "DEBUG"
      akka.loggers = ["akka.testkit.TestEventListener"]
      """)
    ActorSystem("test-actor-system", config)
  }
}

final class SkeletonActorLogSpec
    extends TestKit(SkeletonActorLogSpec.testSystem)
    with WordSpecLike
    with StopSystemAfterAll {

  "skeleton actor" must {
    "log debug when receives a Request message" in {
      val skeletonActorRef = system.actorOf(SkeletonActor.props, "skeleton-test-1")

      EventFilter
        .debug(message = "message: myMessage", occurrences = 1)
        .intercept {
          skeletonActorRef ! RequestMessage("myMessage")
        }
    }
    "log error when receives an invalid message" in {
      val skeletonActorRef = system.actorOf(SkeletonActor.props, "skeleton-test-2")

      EventFilter
        .error(message = "invalid message", occurrences = 1)
        .intercept {
          skeletonActorRef ! "unhandled message"
        }
    }
  }

}
