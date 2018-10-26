package com.kafka.demo
package avro4s

import com.sksamuel.avro4s.{AvroDoc, AvroName, AvroNamespace}

@AvroName("User")
@AvroNamespace("com.kafka.demo")
@AvroDoc("Avro4s User Schema")
private[avro4s] case class User(name: String,
                                @AvroName("favorite_number") favoriteNumber: Option[Int],
                                @AvroName("favorite_color") favoriteColor: Option[String])

object Avro4sExample {

}
