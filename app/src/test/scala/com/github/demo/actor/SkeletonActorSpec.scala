package com.github.demo
package actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}
import com.github.demo.actor.SkeletonActor.{RequestMessage, ResponseMessage}

final class SkeletonActorSpec
    extends TestKit(ActorSystem("test-actor-system"))
    with WordSpecLike
    with MustMatchers
    // using the ImplicitSender trait will automatically set testActor as the sender
    with ImplicitSender
    with StopSystemAfterAll {

  "skeleton actor" must {
    "send Response when receives Request" in {
      val skeletonActorRef = system.actorOf(SkeletonActor.props, "skeleton-test-1")
      skeletonActorRef ! RequestMessage("myMessage")
      expectMsg(ResponseMessage)
    }
  }

}
