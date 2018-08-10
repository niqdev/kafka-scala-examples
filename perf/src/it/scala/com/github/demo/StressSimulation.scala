package com.github.demo

import io.gatling.core.Predef._
import io.gatling.http.Predef.{http => gHttp}

import scala.concurrent.duration._

final class StressSimulation extends Simulation {

  private[this] val httpConf = gHttp
    .baseURL("http://localhost:8080")

  private[this] val scn = scenario("Stress Scenario")
    .exec(gHttp("request-status").get("/status"))

  // scalastyle:off magic.number
  setUp(scn.inject(
    nothingFor(2 seconds),
    atOnceUsers(10),
    rampUsers(10) over (5 seconds),
    constantUsersPerSec(20) during (15 seconds) randomized,
    rampUsersPerSec(10) to 20 during (2 minutes) randomized,
    heavisideUsers(1000) over (20 seconds)
  ).protocols(httpConf))
  // scalastyle:on magic.number

}
