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

package controllers.actions

import base.SpecBase
import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.EnrolmentStoreConnector
import controllers.actions.AuthActionSpec.*
import controllers.routes
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{reset, when}
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core as authClient
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{~, Retrieval}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase {

  class Harness(authAction: IdentifierAction) {

    def onPageLoad(): Action[AnyContent] = authAction {
      _ =>
        Results.Ok
    }
  }

  private val mockAuthConnector: AuthConnector                     = mock[AuthConnector]
  private val mockEnrolmentStoreConnector: EnrolmentStoreConnector = mock[EnrolmentStoreConnector]
  private val mockFrontendAppConfig                                = mock[FrontendAppConfig]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuthConnector)
    reset(mockEnrolmentStoreConnector)

    when(mockFrontendAppConfig.loginUrl).thenReturn("http://localhost:9949/auth-login-stub/gg-sign-in")
    when(mockFrontendAppConfig.loginContinueUrl).thenReturn("http://localhost:9485/manage-transit-movements/view-departure-declarations")
    when(mockFrontendAppConfig.enrolmentKey).thenReturn(ENROLMENT_KEY)
    when(mockFrontendAppConfig.enrolmentIdentifierKey).thenReturn(ENROLMENT_ID_KEY)
    when(mockFrontendAppConfig.eccEnrolmentSplashPage).thenReturn("http://localhost:6750/customs-enrolment-services/ctc/subscribe")
  }

  val ENROLMENT_KEY    = "HMRC-CTC-ORG"
  val ENROLMENT_ID_KEY = "EORINumber"

  private def createEnrolment(key: String, identifierKey: Option[String], id: String, state: String) =
    Enrolment(
      key = key,
      identifiers = identifierKey match {
        case Some(idKey) => Seq(EnrolmentIdentifier(idKey, id))
        case None        => Seq.empty
      },
      state = state
    )

  "Auth Action" - {

    "when the user hasn't logged in" - {
      "must redirect the user to log in " in {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new MissingBearerToken),
          mockFrontendAppConfig,
          bodyParser,
          mockEnrolmentStoreConnector
        )

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(
          result
        ).get mustEqual s"http://localhost:9949/auth-login-stub/gg-sign-in?continue=http%3A%2F%2Flocalhost%3A9485%2Fmanage-transit-movements%2Fview-departure-declarations"
      }
    }

    "when the user's session has expired" - {
      "must redirect the user to log in " in {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new BearerTokenExpired),
          mockFrontendAppConfig,
          bodyParser,
          mockEnrolmentStoreConnector
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(
          result
        ).get mustEqual s"http://localhost:9949/auth-login-stub/gg-sign-in?continue=http%3A%2F%2Flocalhost%3A9485%2Fmanage-transit-movements%2Fview-departure-declarations"
      }
    }

    "when the user doesn't have sufficient enrolments" - {
      "must redirect the user to the unauthorised page" in {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new InsufficientEnrolments),
          mockFrontendAppConfig,
          bodyParser,
          mockEnrolmentStoreConnector
        )

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.UnauthorisedController.onPageLoad().url
      }
    }

    "when the user doesn't have sufficient confidence level" - {
      "must redirect the user to the unauthorised page" in {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new InsufficientConfidenceLevel),
          mockFrontendAppConfig,
          bodyParser,
          mockEnrolmentStoreConnector
        )

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.UnauthorisedController.onPageLoad().url
      }
    }

    "when the user used an unaccepted auth provider" - {
      "must redirect the user to the unauthorised page" in {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new UnsupportedAuthProvider),
          mockFrontendAppConfig,
          bodyParser,
          mockEnrolmentStoreConnector
        )

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.UnauthorisedController.onPageLoad().url
      }
    }

    "when the user has an unsupported affinity group" - {
      "must redirect the user to the unauthorised page" in {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new UnsupportedAffinityGroup),
          mockFrontendAppConfig,
          bodyParser,
          mockEnrolmentStoreConnector
        )

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.UnauthorisedController.onPageLoad().url
      }
    }

    "when the user has an unsupported credential role" - {
      "must redirect the user to the unauthorised page" in {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new UnsupportedCredentialRole),
          mockFrontendAppConfig,
          bodyParser,
          mockEnrolmentStoreConnector
        )

        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.UnauthorisedController.onPageLoad().url
      }
    }

    "when given new enrolments without eori" - {
      "must redirect to unauthorised page" in {
        val newEnrolmentsWithoutEori: Enrolments = Enrolments(
          Set(
            createEnrolment("IR-SA", Some("UTR"), "123", "Activated"),
            createEnrolment(ENROLMENT_KEY, None, "999", "Activated"),
            createEnrolment("IR-CT", Some("UTR"), "456", "Activated")
          )
        )

        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(newEnrolmentsWithoutEori ~ Some("testName")))

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, mockFrontendAppConfig, bodyParser, mockEnrolmentStoreConnector)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.UnauthorisedController.onPageLoad().url
      }
    }

    "when given user has no active new enrolments but new group has" - {
      "must redirect to unauthorised page with group access" in {
        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("testName")))
        when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), eqTo(ENROLMENT_KEY))(any())).thenReturn(Future.successful(true))

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, mockFrontendAppConfig, bodyParser, mockEnrolmentStoreConnector)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.UnauthorisedWithGroupAccessController.onPageLoad().url
      }
    }

    "when given user has no enrolments but group has" - {
      "must redirect to unauthorised page with group access" in {
        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("testName")))
        when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), eqTo(ENROLMENT_KEY))(any())).thenReturn(Future.successful(true))

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, mockFrontendAppConfig, bodyParser, mockEnrolmentStoreConnector)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.UnauthorisedWithGroupAccessController.onPageLoad().url
      }
    }

    "when given both user and group has no enrolments" - {
      "must redirect to unauthorised page without group access" in {
        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("testName")))
        when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), eqTo(ENROLMENT_KEY))(any())).thenReturn(Future.successful(false))

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, mockFrontendAppConfig, bodyParser, mockEnrolmentStoreConnector)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual frontendAppConfig.eccEnrolmentSplashPage
      }
    }

    "when given user has no enrolments and there is no group" - {
      "must redirect to unauthorised page without group access" in {
        when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(Enrolments(Set.empty) ~ None))

        when(mockEnrolmentStoreConnector.checkGroupEnrolments(any(), eqTo(ENROLMENT_KEY))(any())).thenReturn(Future.successful(false))

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, mockFrontendAppConfig, bodyParser, mockEnrolmentStoreConnector)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual frontendAppConfig.eccEnrolmentSplashPage
      }
    }

    "when given new enrolments with eori" - {
      "must return Ok" in {
        val newEnrolmentsWithEori: Enrolments = Enrolments(
          Set(
            createEnrolment("IR-SA", Some("UTR"), "123", "Activated"),
            createEnrolment(ENROLMENT_KEY, Some(ENROLMENT_ID_KEY), "123", "NotYetActivated"),
            createEnrolment(ENROLMENT_KEY, Some(ENROLMENT_ID_KEY), "456", "Activated")
          )
        )

        when(mockAuthConnector.authorise[Enrolments ~ Some[String]](any(), any())(any(), any()))
          .thenReturn(Future.successful(newEnrolmentsWithEori ~ Some("testName")))

        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, mockFrontendAppConfig, bodyParser, mockEnrolmentStoreConnector)
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(fakeRequest)

        status(result) mustEqual OK
      }
    }
  }
}

object AuthActionSpec {

  implicit class RetrievalsUtil[A](val retrieval: A) extends AnyVal {
    def `~`[B](anotherRetrieval: B): A ~ B = authClient.retrieve.~(retrieval, anotherRetrieval)
  }
}

class FakeFailingAuthConnector @Inject() (exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
