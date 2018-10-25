package com.kafka.demo

import org.scalatest.{Matchers, WordSpecLike}

final class SimpleAvroSpec extends WordSpecLike with Matchers {

  private[this] def getUsers(): List[User] = {
    val user1 = new User()
    user1.setName("Alyssa")
    user1.setFavoriteNumber(256)
    // Leave favorite color null

    // Alternate constructor
    val user2 = new User("Ben", 7, "red")

    // Construct via builder
    val user3 = User.newBuilder()
      .setName("Charlie")
      .setFavoriteColor("blue")
      .setFavoriteNumber(null)
      .build()

    List(user1, user2, user3)
  }

  "SimpleAvro" should {

    "serialize and deserialize" in {
      val users = getUsers()
      val path = "data/users.avro"
      SimpleAvro.serializeUsers(users, path)
      SimpleAvro.deserializeUsers(path) shouldBe users
    }

  }

}
