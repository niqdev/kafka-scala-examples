package com.kafka.demo

import java.time.{Duration, LocalDateTime}

import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}

import scala.collection.JavaConverters.asJavaCollectionConverter
import scala.util.{Failure, Success, Try}

object KafkaHelper {
  private[this] val logger = Logger(getClass.getSimpleName)

  def produceMessages[T](f: ((Int, String)) => T, count: Int = 100): Seq[T] =
    (1 to count)
      .map { i =>
        (i, s"Message $i @ ${LocalDateTime.now} on ${Thread.currentThread.getName}")
      }
      .map(f)

  def consume[K, V](consumer: KafkaConsumer[K, V],
                    topic: String,
                    timeoutMills: Long): Unit = {

    logger.info(s"Start to consume from $topic")

    consumer.subscribe(List(topic).asJavaCollection)

    Try {
      while (true) {
        val records: ConsumerRecords[K, V] = consumer.poll(Duration.ofMillis(timeoutMills))
        records.iterator().forEachRemaining { record: ConsumerRecord[K, V] =>
          logger.info(
            s"""
               |message
               |  offset=${record.offset}
               |  partition=${record.partition}
               |  key=${record.key}
               |  value=${record.value}
           """.stripMargin)
        }
      }
    } match {
      case Success(_) =>
        logger.info(s"Finish to consume from $topic")
      case Failure(exception) =>
        logger.error(s"Finish to consume from $topic with error", exception)
    }

    consumer.close()
  }

}
