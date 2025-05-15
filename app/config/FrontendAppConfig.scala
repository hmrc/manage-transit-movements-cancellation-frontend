/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.i18n.Messages
import play.api.mvc.Request
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, service: ServicesConfig) {

  val userResearchUrl: String         = configuration.get[String]("urls.userResearch")
  val showUserResearchBanner: Boolean = configuration.get[Boolean]("banners.showUserResearch")
  val contactHost: String             = configuration.get[String]("contact-frontend.host")

  val signOutUrl: String            = configuration.get[String]("urls.logoutContinue") + configuration.get[String]("urls.feedback")
  lazy val nctsHelpdeskUrl: String  = configuration.get[String]("urls.nctsHelpdesk")
  lazy val nctsEnquiriesUrl: String = configuration.get[String]("urls.nctsEnquiries")

  lazy val authUrl: String          = service.baseUrl("auth")
  lazy val loginUrl: String         = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  lazy val enrolmentProxyUrl: String = service.baseUrl("enrolment-store-proxy") + "/enrolment-store-proxy"

  lazy val enrolmentKey: String           = configuration.get[String]("enrolment.key")
  lazy val enrolmentIdentifierKey: String = configuration.get[String]("enrolment.identifierKey")

  lazy val eccEnrolmentSplashPage: String = configuration.get[String]("urls.eccEnrolmentSplashPage")

  lazy val manageTransitMovementsUrl: String               = configuration.get[String]("urls.manageTransitMovementsFrontend")
  lazy val manageTransitMovementsViewDeparturesUrl: String = s"$manageTransitMovementsUrl/view-departure-declarations"
  lazy val serviceUrl: String                              = s"$manageTransitMovementsUrl/what-do-you-want-to-do"

  lazy val commonTransitConventionTradersUrl: String = configuration.get[Service]("microservice.services.common-transit-convention-traders").fullServiceUrl

  lazy val timeoutSeconds: Int   = configuration.get[Int]("session.timeoutSeconds")
  lazy val countdownSeconds: Int = configuration.get[Int]("session.countdownSeconds")

  lazy val cacheTtl: Int = configuration.get[Int]("mongodb.timeToLiveInSeconds")

  lazy val replaceIndexes: Boolean = configuration.get[Boolean]("feature-flags.replace-indexes")

  lazy val referenceDataUrl: String = configuration.get[Service]("microservice.services.customs-reference-data").fullServiceUrl

  lazy val phase6Enabled: Boolean = configuration.get[Boolean]("feature-flags.phase-6-enabled")

  val encryptionKey: String      = configuration.get[String]("encryption.key")
  val encryptionEnabled: Boolean = configuration.get[Boolean]("encryption.enabled")

  val isTraderTest: Boolean = configuration.get[Boolean]("trader-test.enabled")
  val feedbackEmail: String = configuration.get[String]("trader-test.feedback.email")
  val feedbackForm: String  = configuration.get[String]("trader-test.feedback.link")

  def mailto(implicit request: Request[?], messages: Messages): String = {
    val subject = messages("site.email.subject")
    val body = {
      val newLine      = "%0D%0A"
      val newParagraph = s"$newLine$newLine"
      s"""
         |URL: ${request.uri}$newParagraph
         |Tell us how we can help you here.$newParagraph
         |Give us a brief description of the issue or question, including details like…$newLine
         | - The screens where you experienced the issue$newLine
         | - What you were trying to do at the time$newLine
         | - The information you entered$newParagraph
         |Please include your name and phone number and we’ll get in touch.
         |""".stripMargin
    }

    s"mailto:$feedbackEmail?subject=$subject&body=$body"
  }
}
