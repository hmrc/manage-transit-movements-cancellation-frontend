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

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  val contactHost: String                  = configuration.get[String]("contact-frontend.host")
  val contactFormServiceIdentifier: String = "CTCTraders"

  val trackingConsentUrl: String = configuration.get[String]("microservice.services.tracking-consent-frontend.url")
  val gtmContainer: String       = configuration.get[String]("microservice.services.tracking-consent-frontend.gtm.container")

  val userResearchUrl: String         = configuration.get[String]("urls.userResearch")
  val showUserResearchBanner: Boolean = configuration.get[Boolean]("banners.showUserResearch")

  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"
  val signOutUrl: String             = configuration.get[String]("urls.logoutContinue") + configuration.get[String]("urls.feedback")
  lazy val nctsEnquiriesUrl: String  = configuration.get[String]("urls.nctsEnquiries")
  lazy val nctsHelpdeskUrl: String   = configuration.get[String]("urls.nctsHelpdesk")

  lazy val authUrl: String           = configuration.get[Service]("auth").baseUrl
  lazy val loginUrl: String          = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String  = configuration.get[String]("urls.loginContinue")
  lazy val enrolmentKey: String      = configuration.get[String]("microservice.services.auth.enrolmentKey")
  lazy val enrolmentProxyUrl: String = configuration.get[Service]("microservice.services.enrolment-store-proxy").fullServiceUrl

  lazy val legacyEnrolmentKey: String           = configuration.get[String]("keys.legacy.enrolmentKey")
  lazy val legacyEnrolmentIdentifierKey: String = configuration.get[String]("keys.legacy.enrolmentIdentifierKey")
  lazy val newEnrolmentKey: String              = configuration.get[String]("keys.enrolmentKey")
  lazy val newEnrolmentIdentifierKey: String    = configuration.get[String]("keys.enrolmentIdentifierKey")
  lazy val eccEnrolmentSplashPage: String       = configuration.get[String]("urls.eccEnrolmentSplashPage")

  lazy val manageTransitMovementsUrl: String               = configuration.get[String]("urls.manageTransitMovementsFrontend")
  lazy val manageTransitMovementsViewDeparturesUrl: String = s"$manageTransitMovementsUrl/view-departures"
  lazy val serviceUrl                                      = s"$manageTransitMovementsUrl/what-do-you-want-to-do"

  lazy val departureBaseUrl: String = configuration.get[Service]("microservice.services.departures").baseUrl
  lazy val departureUrl: String     = configuration.get[Service]("microservice.services.departures").fullServiceUrl

  lazy val timeoutSeconds: Int   = configuration.get[Int]("session.timeoutSeconds")
  lazy val countdownSeconds: Int = configuration.get[Int]("session.countdownSeconds")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")
}
