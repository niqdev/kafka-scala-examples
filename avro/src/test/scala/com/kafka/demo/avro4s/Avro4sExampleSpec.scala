package com.kafka.demo
package avro4s

import java.io.File

import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.Schema
import org.scalatest.{Matchers, WordSpecLike}

final class Avro4sExampleSpec extends WordSpecLike with Matchers {

  private[this] val userSchema: Schema = AvroSchema[avro4s.User]

  private[this] def getUsers(): List[User] =
    List(
      avro4s.User("User1", Some(8), None),
      avro4s.User("User2", None, Some("green"))
    )

  "Avro4sExample" should {

    "verify schema" in {
      val schemaPath = "avro/src/main/avro/user.avsc"
      val originalSchema = new Schema.Parser().parse(new File(schemaPath))

      // "doc" field comparison is ignored
      //println(userSchema)
      //println(originalSchema)
      userSchema shouldBe originalSchema
    }

    "serialize and deserialize" in {
      val filePath = "data/users-avro4s.avro"
      val users = getUsers()

      Avro4sExample.serializeUsers(users, userSchema, filePath)
      Avro4sExample.deserializeUsers(userSchema, filePath) shouldBe users
    }

  }

}
