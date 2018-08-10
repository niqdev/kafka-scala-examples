import sbt._

object Dependencies {

  lazy val N = new {
    val `typesafe-akka` = "com.typesafe.akka"
    val circe = "io.circe"
    val refined = "eu.timepit"
    val enumeratum = "com.beachape"
    val prometheus = "io.prometheus"
  }

  lazy val V = new {
    val scala = "2.12.6"

    val akka = "2.5.14"
    val akkaHttp = "10.1.3"

    val circe = "0.9.3"
    val refined = "0.9.2"
    val enumeratum = "1.5.13"

    val prometheus = "0.5.0"

    val scalatest = "3.0.5"
    val scalamock = "3.6.0"
    val gatling = "2.3.1"
  }

  lazy val commonDependencies = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
    "com.typesafe" % "config" % "1.3.3",

    "org.scalatest" %% "scalatest" % V.scalatest % Test,
    "org.scalamock" %% "scalamock-scalatest-support" % V.scalamock % Test
  )

  lazy val appDependencies = commonDependencies ++ Seq(
    N.`typesafe-akka` %% "akka-actor" % V.akka,
    N.`typesafe-akka` %% "akka-stream" % V.akka,
    N.`typesafe-akka` %% "akka-http" % V.akkaHttp,
    N.`typesafe-akka` %% "akka-slf4j" % V.akka,

    "org.typelevel" %% "cats-core" % "1.2.0",

    N.circe %% "circe-core" % V.circe,
    N.circe %% "circe-generic" % V.circe,
    N.circe %% "circe-parser" % V.circe,
    N.circe %% "circe-java8" % V.circe,
    N.circe %% "circe-refined" % V.circe,
    "de.heikoseeberger" %% "akka-http-circe" % "1.20.1",

    N.refined %% "refined" % V.refined,
    N.refined %% "refined-cats" % V.refined,
    N.enumeratum %% "enumeratum" % V.enumeratum,
    N.enumeratum %% "enumeratum-circe" % V.enumeratum,

    // ERROR javax.ws.rs#javax.ws.rs-api;2.1!javax.ws.rs-api.${packaging.type}
    "org.apache.kafka" %% "kafka-streams-scala" % "2.0.0" exclude("javax.ws.rs", "javax.ws.rs-api"),

    N.prometheus % "simpleclient" % V.prometheus,
    N.prometheus % "simpleclient_common" % V.prometheus,
    N.prometheus % "simpleclient_hotspot" % V.prometheus,

    N.`typesafe-akka` %% "akka-testkit" % V.akka % Test,
    N.`typesafe-akka` %% "akka-http-testkit" % V.akkaHttp % Test
  )

  lazy val cliDependencies = commonDependencies ++ Seq(
    "com.github.scopt" %% "scopt" % "3.7.0"
  )

  lazy val perfDependencies = commonDependencies ++ Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts" % V.gatling % "test,it",
    "io.gatling" % "gatling-test-framework" % V.gatling % "test,it"
  )

}
