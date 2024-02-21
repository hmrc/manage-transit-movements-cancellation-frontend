import sbt.Setting
import scoverage.ScoverageKeys

object CodeCoverageSettings {

  private val excludedFiles: Seq[String] = Seq(
    "<empty>",
    "Reverse.*",
    ".*handlers.*",
    ".*repositories.*",
    ".*BuildInfo.*",
    ".*javascript.*",
    ".*Routes.*",
    ".*GuiceInjector",
    ".*ControllerConfiguration"
  )

  private val excludedPackages: Seq[String] = Seq(
    "testOnly.*",
    "testOnlyDoNotUseInAppConf.*",
    "views\\.html\\.components.*",
    "views\\.html\\.resources.*",
    "views\\.html\\.templates.*",
    "views\\.utils.*"
  )

  val settings: Seq[Setting[_]] = Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 85,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}
