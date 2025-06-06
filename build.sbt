import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "manage-transit-movements-cancellation-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.5.0"
ThisBuild / scalafmtOnCompile := true

lazy val microservice = (project in file("."))
  .enablePlugins(
    PlayScala,
    SbtDistributablesPlugin,
    ScalaxbPlugin
  )
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .configs(A11yTest)
  .settings(inConfig(A11yTest)(org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings) *)
  .settings(headerSettings(A11yTest) *)
  .settings(automateHeaderSettings(A11yTest))
  .settings(
    name := appName,
    RoutesKeys.routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "controllers.routes._",
      "views.html.helper.CSPNonce",
      "templates._"
    ),
    PlayKeys.playDefaultPort := 10122,
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:src=routes/.*:s",
      "-Wconf:src=html/.*:s",
      "-Wconf:src=src_managed/.*:s"
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    Assets / pipelineStages := Seq(digest)
  )
  .settings(CodeCoverageSettings.settings *)
  .settings(
    Compile / scalaxb / scalaxbXsdSource := new File("./conf/xsd"),
    Compile / scalaxb / scalaxbDispatchVersion := "1.1.3",
    Compile / scalaxb / scalaxbPackageName := "generated"
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(
    libraryDependencies ++= AppDependencies.test,
    DefaultBuildSettings.itSettings()
  )
