package com.kafka.demo
package original

import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.scalatest.{Matchers, WordSpecLike}

final class AvroGenericRecordSpec extends WordSpecLike with Matchers {

  // setting a non-existent field throws AvroRuntimeException
  private[this] def getUsers(schema: Schema): List[GenericRecord] = {
    // Leave favorite color null
    val user1: GenericRecord = new GenericData.Record(schema)
    user1.put("name", "Alyssa")
    user1.put("favorite_number", 256)

    val user2: GenericRecord = new GenericData.Record(schema)
    user2.put("name", "Ben")
    user2.put("favorite_number", 7)
    user2.put("favorite_color", "red")

    List(user1, user2)
  }

  "AvroGenericRecord" should {

    "serialize and deserialize" in {
      val schemaPath = "avro/src/main/avro/user.avsc"
      val filePath = "data/users-generic-record.avro"
      val schema = AvroGenericRecord.getSchema(schemaPath)
      val users = getUsers(schema)

      AvroGenericRecord.serialize(users, schemaPath, filePath)
      AvroGenericRecord.deserialize(schemaPath, filePath) shouldBe users
    }

  }

}
