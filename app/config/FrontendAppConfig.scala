/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, service: ServicesConfig) {

  val contactHost: String                  = configuration.get[String]("contact-frontend.host")
  val contactFormServiceIdentifier: String = "CTCTraders"

  val trackingConsentUrl: String = configuration.get[String]("tracking-consent-frontend.url")
  val gtmContainer: String       = configuration.get[String]("tracking-consent-frontend.gtm.container")

  val userResearchUrl: String         = configuration.get[String]("urls.userResearch")
  val showUserResearchBanner: Boolean = configuration.get[Boolean]("banners.showUserResearch")

  private val host: String = configuration.get[String]("host")

  def feedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  val signOutUrl: String           = configuration.get[String]("urls.logoutContinue") + configuration.get[String]("urls.feedback")
  lazy val nctsHelpdeskUrl: String = configuration.get[String]("urls.nctsHelpdesk")

  lazy val authUrl: String          = service.baseUrl("auth")
  lazy val loginUrl: String         = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  lazy val enrolmentProxyUrl: String = service.baseUrl("enrolment-store-proxy") + "/enrolment-store-proxy"

  lazy val legacyEnrolmentKey: String           = configuration.get[String]("keys.legacy.enrolmentKey")
  lazy val legacyEnrolmentIdentifierKey: String = configuration.get[String]("keys.legacy.enrolmentIdentifierKey")

  lazy val newEnrolmentKey: String           = configuration.get[String]("keys.enrolmentKey")
  lazy val newEnrolmentIdentifierKey: String = configuration.get[String]("keys.enrolmentIdentifierKey")

  lazy val eccEnrolmentSplashPage: String = configuration.get[String]("urls.eccEnrolmentSplashPage")

  lazy val manageTransitMovementsUrl: String               = configuration.get[String]("urls.manageTransitMovementsFrontend")
  lazy val manageTransitMovementsViewDeparturesUrl: String = s"$manageTransitMovementsUrl/view-departures"
  lazy val serviceUrl: String                              = s"$manageTransitMovementsUrl/what-do-you-want-to-do"

  lazy val departureBaseUrl: String = service.baseUrl("departures")
  lazy val departureUrl: String     = departureBaseUrl + "/transits-movements-trader-at-departure"

  lazy val timeoutSeconds: Int   = configuration.get[Int]("session.timeoutSeconds")
  lazy val countdownSeconds: Int = configuration.get[Int]("session.countdownSeconds")

  lazy val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  lazy val replaceIndexes: Boolean = configuration.get[Boolean]("feature-flags.replace-indexes")
}
