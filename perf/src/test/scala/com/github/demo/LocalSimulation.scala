package com.github.demo

import io.gatling.core.Predef._
import io.gatling.http.Predef.{http => gHttp, _}

final class LocalSimulation extends Simulation {

  private[this] val httpConf = gHttp
    .baseURL("http://localhost:8080")

  private[this] val scn = scenario("Local Scenario")
    .exec(
      gHttp("request-status")
        .get("/status")
        .check(
          status.is(200),
          jsonPath("$..value").is("OK")
        ))
    .pause(1)

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))

}
