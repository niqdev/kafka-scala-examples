lazy val I = new {
  val organization = "com.kafka.demo"
  val name = "kafka-demo"
  val version = "0.1"
}

lazy val V = new {
  val scala = "2.12.7"

  val logback = "1.2.3"
  val scalaLogging = "3.9.0"

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
  // test dependencies are excluded by default
  .dependsOn(common % "compile->compile;test->test")
  .enablePlugins(SbtAvro)

lazy val `schema-registry` = project.in(file("schema-registry"))
  .dependsOn(common % "compile->compile;test->test")

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
