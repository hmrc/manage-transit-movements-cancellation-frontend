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
    "views\\.html\\.components.*",
    "views\\.html\\.resources.*",
    "views\\.html\\.templates.*",
    ".*scalaxb.*",
    ".*generated.*"
  )

  val settings: Seq[Setting[?]] = Seq(
    ScoverageKeys.coverageExcludedFiles := excludedFiles.mkString(";"),
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}
