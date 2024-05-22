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

package controllers

import base.SpecBase
import connectors.DepartureMovementConnector
import generated.CC014CType
import generators.Generators
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, verifyNoInteractions, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.CancellationSubmissionConfirmationView

import scala.concurrent.Future

class CancellationSubmissionConfirmationControllerSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private lazy val cancellationSubmittedRoute: String =
    routes.CancellationSubmissionConfirmationController.onPageLoad(departureId, lrn).url

  private val mockConnector = mock[DepartureMovementConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConnector)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMovementConnector].toInstance(mockConnector))

  "CancellationSubmissionConfirmation Controller" - {

    "must return OK and the correct view for a GET" in {
      forAll(arbitrary[CC014CType]) {
        ie014 =>
          beforeEach()

          when(mockDepartureMessageService.getIE014(any())(any(), any()))
            .thenReturn(Future.successful(Some(ie014)))

          when(mockSessionRepository.remove(any(), any()))
            .thenReturn(Future.successful(true))

          val request = FakeRequest(GET, cancellationSubmittedRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[CancellationSubmissionConfirmationView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(lrn)(request, messages).toString

          verify(mockDepartureMessageService).getIE014(eqTo(departureId))(any(), any())
          verify(mockSessionRepository).remove(eqTo(departureId), eqTo(eoriNumber))
      }
    }

    "must redirect to tech difficulties" - {
      "when IE014 not found" in {
        when(mockDepartureMessageService.getIE014(any())(any(), any()))
          .thenReturn(Future.successful(None))

        val request = FakeRequest(GET, cancellationSubmittedRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.routes.ErrorController.technicalDifficulties().url

        verifyNoInteractions(mockSessionRepository)
      }
    }
  }
}
