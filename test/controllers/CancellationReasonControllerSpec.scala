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

package controllers

import base.SpecBase
import matchers.JsonMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.CancellationReasonPage
import play.api.mvc.Call
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import services.responses.InvalidState
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class CancellationReasonControllerSpec extends SpecBase with MockitoSugar with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  lazy val cancellationReasonRoute = routes.CancellationReasonController.onPageLoad(departureId).url

  "CancellationReason Controller" - {

    "must return OK and the correct view for a GET" in {
      checkCancellationStatus()

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, cancellationReasonRoute)
      val result  = route(app, request).value

      status(result) mustEqual OK

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      checkCancellationStatus()

      val userAnswers = emptyUserAnswers.set(CancellationReasonPage(departureId), "answer").success.value

      dataRetrievalWithData(userAnswers)

      val request = FakeRequest(GET, cancellationReasonRoute)
      val result  = route(app, request).value

      status(result) mustEqual OK

    }

    "must redirect to the next page when valid data is submitted" in {

      checkCancellationStatus()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      when(mockSubmissionService.submitCancellation(any())(any()))
        .thenReturn(Future.successful(Right(HttpResponse(Helpers.ACCEPTED, ""))))

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, cancellationReasonRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual s"${controllers.routes.CancellationSubmissionConfirmationController.onPageLoad(departureId)}"
    }

    "must return InternalServerError when the submission fails" in {

      checkCancellationStatus()

      when(mockSubmissionService.submitCancellation(any())(any()))
        .thenReturn(Future.successful(Left(InvalidState)))

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, cancellationReasonRoute)
        .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      checkCancellationStatus()

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, cancellationReasonRoute).withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

    }
  }
}
