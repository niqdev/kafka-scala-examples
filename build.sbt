import Settings._

lazy val common = project.in(file("common"))
  .settings(commonSettings)

lazy val app = project.in(file("app"))
  .enablePlugins(JavaServerAppPackaging)
  .settings(appSettings)
  .dependsOn(common)

lazy val cli = project.in(file("cli"))
  .settings(cliSettings)
  .dependsOn(common)

lazy val perf = project.in(file("perf"))
  .enablePlugins(GatlingPlugin)
  .settings(perfSettings)
  .dependsOn(common)

lazy val `kafka-stream-demo` = project.in(file("."))
  // TODO issue AutomateHeaderPlugin https://github.com/sbt/sbt-header/issues/153
  .enablePlugins(SiteScaladocPlugin, ScalaUnidocPlugin)
  .settings(rootSettings)
  .aggregate(common, app, cli, perf)
