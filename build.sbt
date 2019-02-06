lazy val I = new {
  val organization = "com.kafka.demo"
  val name = "kafka-demo"
  val version = "0.1"
}

lazy val N = new {
  val cakeSolutions = "net.cakesolutions"
  val confluent = "io.confluent"
  val kafka = "org.apache.kafka"
}

lazy val V = new {
  val scala = "2.12.8"

  val logback = "1.2.3"
  val scalaLogging = "3.9.0"

  val avro4s = "2.0.4"
  val cakeSolutions = "2.0.0"
  val kafka = "2.0.0"
  val confluent = "5.0.0"

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
      N.cakeSolutions %% "scala-kafka-client" % V.cakeSolutions,

      N.cakeSolutions %% "scala-kafka-client-testkit" % V.cakeSolutions % Test
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
      N.kafka % "kafka-clients" % V.kafka,
      N.confluent % "kafka-avro-serializer" % V.confluent,
      N.confluent % "kafka-schema-registry-client" % V.confluent,

      N.cakeSolutions %% "scala-kafka-client" % V.cakeSolutions,
      N.cakeSolutions %% "scala-kafka-client-testkit" % V.cakeSolutions % Test
    ),

    // sbt-avrohugger: SpecificRecord
    sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue
  )

lazy val streams = project.in(file("streams"))
  .dependsOn(common % "compile->compile;test->test")
  .settings(
    libraryDependencies ++= Seq(
      N.kafka % "kafka-streams" % V.kafka,
      N.kafka %% "kafka-streams-scala" % V.kafka,

      //N.kafka % "kafka-clients" % V.kafka % Test classifier "test",
      //N.kafka % "kafka-streams" % V.kafka % Test classifier "test",
      N.kafka % "kafka-streams-test-utils" % V.kafka % Test
    ))

lazy val root = project.in(file("."))
  .aggregate(avro, kafka, `schema-registry`, streams)
  .settings(
    inThisBuild(List(
      organization := I.organization,
      scalaVersion := V.scala,
      version := I.version,
    )),

    name := I.name
  )
