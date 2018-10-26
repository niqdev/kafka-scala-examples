package com.kafka.demo
package original

import java.io.File

import com.typesafe.scalalogging.Logger
import org.apache.avro.Schema
import org.apache.avro.file.{DataFileReader, DataFileWriter}
import org.apache.avro.generic.{GenericDatumReader, GenericDatumWriter, GenericRecord}
import org.apache.avro.io.{DatumReader, DatumWriter}

import scala.collection.JavaConverters.asScalaIteratorConverter

/*
 * Serializing and deserializing without code generation
 *
 * https://avro.apache.org/docs/current/gettingstartedjava.html
 */
object AvroGenericRecord {
  private[this] val logger = Logger(getClass.getSimpleName)

  def getSchema(path: String): Schema =
    new Schema.Parser().parse(new File(path))

  def serialize(genericRecords: List[GenericRecord], schemaPath: String, filePath: String): Unit = {
    val schema: Schema = getSchema(schemaPath)
    val file: File = Files.initFile(filePath)

    val datumWriter: DatumWriter[GenericRecord] = new GenericDatumWriter[GenericRecord](schema)
    val dataFileWriter: DataFileWriter[GenericRecord] = new DataFileWriter[GenericRecord](datumWriter)
    dataFileWriter.create(schema, file)
    genericRecords.foreach { genericRecord =>
      logger.debug(s"serialize GenericRecord=[$genericRecord]")
      dataFileWriter.append(genericRecord)
    }
    dataFileWriter.close()
  }

  def deserialize(schemaPath: String, filePath: String): List[GenericRecord] = {
    val schema: Schema = getSchema(schemaPath)
    val file: File = Files.initFile(filePath)

    val datumReader: DatumReader[GenericRecord] = new GenericDatumReader[GenericRecord](schema)
    val dataFileReader: DataFileReader[GenericRecord] = new DataFileReader[GenericRecord](file, datumReader)
    val genericRecords = dataFileReader.iterator().asScala.toList
    dataFileReader.close()

    genericRecords.foreach { genericRecord =>
      logger.debug(s"deserialize genericRecord=[$genericRecord]")
    }
    genericRecords
  }

}
