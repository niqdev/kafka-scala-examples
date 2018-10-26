package com.kafka.demo

import com.typesafe.scalalogging.Logger
import org.apache.avro.file.{DataFileReader, DataFileWriter}
import org.apache.avro.io.{DatumReader, DatumWriter}
import org.apache.avro.specific.{SpecificDatumReader, SpecificDatumWriter}

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
    val userDatumWriter: DatumWriter[User] = new SpecificDatumWriter[User](classOf[User])
    val dataFileWriter: DataFileWriter[User] = new DataFileWriter(userDatumWriter)
    dataFileWriter.create(users.head.getSchema, Files.initFile(filePath))
    //users.foreach(dataFileWriter.append)
    users.foreach { user =>
      logger.debug(s"serialize user=[$user]")
      dataFileWriter.append(user)
    }
    dataFileWriter.close()
  }

  def deserializeUsers(filePath: String): List[User] = {
    val userDatumReader: DatumReader[User] = new SpecificDatumReader[User](classOf[User])
    val dataFileReader: DataFileReader[User] = new DataFileReader(Files.initFile(filePath), userDatumReader)
    val users = dataFileReader.iterator().asScala.toList
    users.foreach { user =>
      logger.debug(s"deserialize user=[$user]")
    }
    users
  }

}
