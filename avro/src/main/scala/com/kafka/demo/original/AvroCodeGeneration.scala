package com.kafka.demo
package original

import java.io.File

import com.typesafe.scalalogging.Logger
import org.apache.avro.file.{ DataFileReader, DataFileWriter }
import org.apache.avro.io.{ DatumReader, DatumWriter }
import org.apache.avro.specific.{ SpecificDatumReader, SpecificDatumWriter }

import scala.collection.JavaConverters.asScalaIteratorConverter

/*
 * Serializing and deserializing with code generation
 *
 * https://avro.apache.org/docs/current/gettingstartedjava.html
 * https://github.com/sbt/sbt-avro
 */
object AvroCodeGeneration {
  private[this] val logger = Logger(getClass.getSimpleName)

  def serializeUsers(users: List[User], filePath: String): Unit = {
    val file: File = Files.initFile(filePath)

    val userDatumWriter: DatumWriter[User]   = new SpecificDatumWriter[User](classOf[User])
    val dataFileWriter: DataFileWriter[User] = new DataFileWriter(userDatumWriter)
    dataFileWriter.create(users.head.getSchema, file)
    //users.foreach(dataFileWriter.append)
    users.foreach { user =>
      logger.debug(s"serialize user=[$user]")
      dataFileWriter.append(user)
    }
    dataFileWriter.close()
  }

  def deserializeUsers(filePath: String): List[User] = {
    val file: File = Files.initFile(filePath)

    val userDatumReader: DatumReader[User]   = new SpecificDatumReader[User](classOf[User])
    val dataFileReader: DataFileReader[User] = new DataFileReader(file, userDatumReader)
    val users                                = dataFileReader.iterator().asScala.toList
    dataFileReader.close()

    users.foreach { user =>
      logger.debug(s"deserialize user=[$user]")
    }
    users
  }

}
