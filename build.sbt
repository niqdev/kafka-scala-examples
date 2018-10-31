lazy val I = new {
  val organization = "com.kafka.demo"
  val name = "kafka-demo"
  val version = "0.1"
}

lazy val N = new {
  val confluent = "io.confluent"
}

lazy val V = new {
  val scala = "2.12.7"

  val logback = "1.2.3"
  val scalaLogging = "3.9.0"

  val avro4s = "2.0.2"
  val confluent = "5.0.0"
  val kafkaClient = "2.0.0"

  val scalatest = "3.0.5"
}

lazy val common = project.in(file("common"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % V.logback,
      "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging,

      "org.scalatest" %% "scalatest" % V.scalatest % Test
    ))

lazy val avro = project.in(file("avro"))
  // test dependencies are excluded by default otherwise
  .dependsOn(common % "compile->compile;test->test")
  .enablePlugins(SbtAvro)
  .settings(
    libraryDependencies ++= Seq(
      "com.sksamuel.avro4s" %% "avro4s-core" % V.avro4s
    ))

lazy val `schema-registry` = project.in(file("schema-registry"))
  .dependsOn(common % "compile->compile;test->test")
  .settings(
    resolvers ++= Seq(
      "confluent" at "https://packages.confluent.io/maven/",
      Resolver.bintrayRepo("cakesolutions", "maven")
    ),

    libraryDependencies ++= Seq(
      N.confluent % "kafka-schema-registry-client" % V.confluent,
      N.confluent % "kafka-avro-serializer" % V.confluent,
      "net.cakesolutions" %% "scala-kafka-client" % V.kafkaClient
    ))

lazy val root = project.in(file("."))
  .aggregate(avro, `schema-registry`)
  .settings(
    inThisBuild(List(
      organization := I.organization,
      scalaVersion := V.scala,
      version := I.version,
    )),

    name := I.name
  )
