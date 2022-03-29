import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-play-28"            % "0.62.0",
    "uk.gov.hmrc"          %% "logback-json-logger"           % "5.2.0",
    "uk.gov.hmrc"          %% "play-conditional-form-mapping" % "1.11.0-play-28",
    "uk.gov.hmrc"          %% "bootstrap-frontend-play-28"    % "5.21.0",
    "uk.gov.hmrc"          %% "play-frontend-hmrc"            % "3.8.0-play-28",
    "com.lucidchart"       %% "xtract"                        % "2.2.1"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-28"  % "0.62.0",
    "org.scalatest"               %% "scalatest"                % "3.2.11",
    "org.scalatestplus.play"      %% "scalatestplus-play"       % "5.1.0",
    "org.pegdown"                 %  "pegdown"                  % "1.6.0",
    "org.jsoup"                   %  "jsoup"                    % "1.14.3",
    "com.typesafe.play"           %% "play-test"                % PlayVersion.current,
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2",
    "org.mockito"                 %  "mockito-core"             % "4.4.0",
    "org.scalatestplus"           %% "mockito-3-2"              % "3.1.2.0",
    "org.scalacheck"              %% "scalacheck"               % "1.15.4",
    "org.scalatestplus"           %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "com.github.tomakehurst"      %  "wiremock-standalone"      % "2.27.2",
    "com.vladsch.flexmark"        %  "flexmark-all"             % "0.62.2"

  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
