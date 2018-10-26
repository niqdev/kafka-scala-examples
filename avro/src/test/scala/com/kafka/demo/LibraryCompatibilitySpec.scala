package com.kafka.demo

import com.kafka.demo.avro4s.Avro4sExample
import com.kafka.demo.original.{AvroCodeGeneration, AvroGenericRecord}
import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.scalatest.{Matchers, WordSpecLike}

final class LibraryCompatibilitySpec extends WordSpecLike with Matchers {

  private[this] val userSchema: Schema = AvroSchema[avro4s.User]

  "LibraryCompatibility" should {

    "verify serialization with code generation and deserialization with avro4s" in {
      val filePath = "data/users-code-generation-from.avro"

      val userCodeGeneration = User.newBuilder()
        .setName("userCodeGeneration")
        .setFavoriteNumber(null)
        .setFavoriteColor("blue")
        .build()
      val expectedUser = avro4s.User("userCodeGeneration", None, Some("blue"))

      AvroCodeGeneration.serializeUsers(List(userCodeGeneration), filePath)
      Avro4sExample.deserializeUsers(userSchema, filePath) shouldBe List(expectedUser)
    }

    "verify serialization with GenericRecord and deserialization with avro4s" in {
      val schemaPath = "avro/src/main/avro/user.avsc"
      val filePath = "data/users-generic-record-from.avro"

      val schemaGenericRecord = AvroGenericRecord.getSchema(schemaPath)
      val userGenericRecord: GenericRecord = new GenericData.Record(schemaGenericRecord)
      userGenericRecord.put("name", "userGenericRecord")
      userGenericRecord.put("favorite_number", 8)
      userGenericRecord.put("favorite_color", "red")
      val expectedUser = avro4s.User("userGenericRecord", Some(8), Some("red"))

      AvroGenericRecord.serialize(List(userGenericRecord), schemaPath, filePath)
      Avro4sExample.deserializeUsers(userSchema, filePath) shouldBe List(expectedUser)
    }

    "verify serialization with avro4s and deserialization with code generation" in {
      val filePath = "data/users-code-generation-to.avro"

      val userAvro4s = avro4s.User("user1Avro4s", Some(8), Some("green"))
      val expectedUser = User.newBuilder()
        .setName("user1Avro4s")
        .setFavoriteNumber(8)
        .setFavoriteColor("green")
        .build()

      Avro4sExample.serializeUsers(List(userAvro4s), userSchema, filePath)
      AvroCodeGeneration.deserializeUsers(filePath) shouldBe List(expectedUser)
    }

    "verify serialization with avro4s and deserialization with GenericRecord" in {
      val schemaPath = "avro/src/main/avro/user.avsc"
      val filePath = "data/users-generic-record-to.avro"

      val userAvro4s = avro4s.User("user2Avro4s", Some(1), Some("red"))
      val schemaGenericRecord = AvroGenericRecord.getSchema(schemaPath)
      val expectedUser: GenericRecord = new GenericData.Record(schemaGenericRecord)
      expectedUser.put("name", "user2Avro4s")
      expectedUser.put("favorite_number", 1)
      expectedUser.put("favorite_color", "red")

      Avro4sExample.serializeUsers(List(userAvro4s), userSchema, filePath)
      AvroGenericRecord.deserialize(schemaPath, filePath) shouldBe List(expectedUser)
    }

  }

}
