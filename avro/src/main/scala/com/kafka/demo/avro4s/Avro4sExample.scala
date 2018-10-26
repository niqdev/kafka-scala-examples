package com.kafka.demo
package avro4s

import java.io.File

import com.sksamuel.avro4s._
import com.typesafe.scalalogging.Logger
import org.apache.avro.Schema

object Avro4sExample {
  private[this] val logger = Logger(getClass.getSimpleName)

  def serializeUsers(users: List[User], schema: Schema, filePath: String): Unit = {
    val file: File = Files.initFile(filePath)

    val os = AvroOutputStream.data[User].to(file).build(schema)
    os.write(users)
    users.foreach { user =>
      logger.debug(s"serialize user=[$user]")
    }
    os.flush()
    os.close()
  }

  def deserializeUsers(schema: Schema, filePath: String): List[User] = {
    val file: File = Files.initFile(filePath)

    val is = AvroInputStream.data[User].from(file).build(schema)
    val users = is.iterator.toList
    is.close()

    users.foreach { user =>
      logger.debug(s"deserialize user=[$user]")
    }
    users
  }

}
