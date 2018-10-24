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

lazy val app = project.in(file("app"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % V.logback,
      "com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging,

      "org.scalatest" %% "scalatest" % V.scalatest % Test
  ))

lazy val root = project.in(file("."))
  .aggregate(app)
  .settings(
    inThisBuild(List(
      organization := I.organization,
      scalaVersion := V.scala,
      version := I.version,
    )),

    name := I.name
  )
