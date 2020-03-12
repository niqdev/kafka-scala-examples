lazy val V = new {
  val scala = "2.12.10"

  val logback = "1.2.3"
  val scalaLogging = "3.9.2"

  val avro4s = "3.0.9"
  val kafka = "2.4.1"
  val confluent = "5.4.1"

  val circe = "0.13.0"
  val catsCore = "2.1.1"
  val catsEffect = "2.1.2"
  val zio = "1.0.0-RC18-2"

  // compatibility issues
  val cakeSolutions = new {
    val version = "2.0.0"
    val kafka = "2.0.0"
    val confluent = "5.0.0"
  }

  val scalaTest = "3.1.0"
  val embeddedKafka = "5.4.1"
}

lazy val common = project.in(file("common"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % V.logback,
      "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging,

      "org.scalatest" %% "scalatest" % V.scalaTest % Test
    ))

lazy val avro = project.in(file("avro"))
  // test dependencies are excluded by default otherwise
  .dependsOn(common % "compile->compile;test->test")
  .enablePlugins(SbtAvro)
  .disablePlugins(SbtAvrohugger)
  .settings(
    libraryDependencies ++= Seq(
      "com.sksamuel.avro4s" %% "avro4s-core" % V.avro4s
    ))

lazy val kafka = project.in(file("kafka"))
  .dependsOn(common % "compile->compile;test->test")
  .settings(
    resolvers ++= Seq(
      Resolver.bintrayRepo("cakesolutions", "maven")
    ),

    libraryDependencies ++= Seq(
      "net.cakesolutions" %% "scala-kafka-client" % V.cakeSolutions.version,

      "net.cakesolutions" %% "scala-kafka-client-testkit" % V.cakeSolutions.version % Test
    ))

lazy val `schema-registry` = project.in(file("schema-registry"))
  .dependsOn(common % "compile->compile;test->test")
  .enablePlugins(SbtAvrohugger)
  // avoid issue XXX is already defined as class XXX
  .disablePlugins(SbtAvro)
  .settings(
    resolvers ++= Seq(
      "confluent" at "https://packages.confluent.io/maven/",
      Resolver.bintrayRepo("cakesolutions", "maven")
    ),

    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-clients" % V.cakeSolutions.kafka,
      "io.confluent" % "kafka-avro-serializer" % V.cakeSolutions.confluent,
      "io.confluent" % "kafka-schema-registry-client" % V.cakeSolutions.confluent,

      "net.cakesolutions" %% "scala-kafka-client" % V.cakeSolutions.version,
      "net.cakesolutions" %% "scala-kafka-client-testkit" % V.cakeSolutions.version % Test
    ),

    // sbt-avrohugger: SpecificRecord
    sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue
  )

lazy val streams = project.in(file("streams"))
  .dependsOn(common % "compile->compile;test->test")
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-streams" % V.kafka,
      "org.apache.kafka" %% "kafka-streams-scala" % V.kafka,

      "org.apache.kafka" % "kafka-streams-test-utils" % V.kafka % Test
    ))

lazy val `streams-json-avro` = project.in(file("streams-json-avro"))
  .dependsOn(common % "compile->compile;test->test")
  .settings(
    resolvers ++= Seq(
      "confluent" at "https://packages.confluent.io/maven/"
    ),

    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka-streams-scala" % V.kafka,
      "io.confluent" % "kafka-streams-avro-serde" % V.confluent,

      "io.circe" %% "circe-core" % V.circe,
      "io.circe" %% "circe-generic" % V.circe,
      "io.circe" %% "circe-parser" % V.circe,

      "com.sksamuel.avro4s" %% "avro4s-core" % V.avro4s,

      "io.github.embeddedkafka" %% "embedded-kafka-schema-registry" % V.embeddedKafka % Test
    ))

/* TODO
- cats-kafka-streams
- zio-kafka-streams
- codec: avro4s / avrohugger / circe
- testing
- sbt-avro4s
*/
lazy val `fp-kafka-streams` = project.in(file("fp-kafka-streams"))
  .dependsOn(common % "compile->compile;test->test")
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % V.catsCore,
      "org.typelevel" %% "cats-effect" % V.catsEffect,
      "dev.zio" %% "zio" % V.zio
    ))

lazy val root = project.in(file("."))
  .aggregate(avro, kafka, `schema-registry`, streams, `streams-json-avro`, `fp-kafka-streams`)
  .settings(
    organization := "com.kafka.demo",
    name := "kafka-scala-example",
    scalaVersion := V.scala,
    version := "0.1",
    parallelExecution := false
  )
