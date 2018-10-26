package com.kafka.demo
package avro4s

import java.io.File

import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.Schema
import org.scalatest.{Matchers, WordSpecLike}

final class Avro4sExampleSpec extends WordSpecLike with Matchers {

  "Avro4sExample" should {

    "verify schema" in {
      val schemaPath = "avro/src/main/avro/user.avsc"
      val originalSchema = new Schema.Parser().parse(new File(schemaPath))

      val schema: Schema = AvroSchema[avro4s.User]

      // "doc" field comparison is ignored
      println(schema)
      println(originalSchema)
      schema shouldBe originalSchema
    }

  }

}
