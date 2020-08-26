package com.kafka.demo
package streams

import java.util

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.apache.kafka.streams.errors.ProductionExceptionHandler.ProductionExceptionHandlerResponse
import org.slf4j.LoggerFactory

final class LogAndFailProductionExceptionHandler extends ProductionExceptionHandler {
  private[this] val log = LoggerFactory.getLogger(classOf[LogAndFailProductionExceptionHandler])

  override def handle(
    record: ProducerRecord[Array[Byte], Array[Byte]],
    exception: Exception
  ): ProductionExceptionHandlerResponse = {
    log.warn(
      s"""
         |Exception caught during Production
         |topic: ${record.topic}
         |partition: ${record.partition}
         |headers: ${record.headers}
         |timestamp: ${record.timestamp}
         |key: ${record.key}
         |value: ${record.value}
       """.stripMargin,
      exception
    )
    ProductionExceptionHandlerResponse.FAIL
  }

  override def configure(configs: util.Map[String, _]): Unit = {}
}
