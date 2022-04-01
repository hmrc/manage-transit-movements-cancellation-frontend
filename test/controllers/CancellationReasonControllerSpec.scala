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
import forms.CancellationReasonFormProvider
import matchers.JsonMatchers
import models.Constants.commentMaxLength
import models.UserAnswers
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.CancellationReasonPage
import play.api.data.Form
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import services.responses.InvalidState
import uk.gov.hmrc.http.HttpResponse
import views.html.CancellationReasonView

import scala.concurrent.Future

class CancellationReasonControllerSpec extends SpecBase with MockitoSugar with JsonMatchers {

  private lazy val cancellationReasonRoute: String = routes.CancellationReasonController.onPageLoad(departureId).url
  private val form: Form[String]                   = new CancellationReasonFormProvider()()

  private val validAnswer = "answer"

  "CancellationReason Controller" - {

    "must return OK and the correct view for a GET" in {

      checkCancellationStatus()

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, cancellationReasonRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CancellationReasonView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, departureId, lrn, commentMaxLength)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      checkCancellationStatus()

      val userAnswers = emptyUserAnswers.setValue(CancellationReasonPage(departureId), validAnswer)

      dataRetrievalWithData(userAnswers)

      val request = FakeRequest(GET, cancellationReasonRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CancellationReasonView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), departureId, lrn, commentMaxLength)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      checkCancellationStatus()

      when(mockSubmissionService.submitCancellation(any())(any()))
        .thenReturn(Future.successful(Right(HttpResponse(Helpers.ACCEPTED, ""))))

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, cancellationReasonRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.CancellationSubmissionConfirmationController.onPageLoad(departureId).url

      val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(uaCaptor.capture)
      uaCaptor.getValue.get(CancellationReasonPage(departureId)).get mustBe validAnswer
    }

    "must return InternalServerError when the submission fails" in {

      checkCancellationStatus()

      when(mockSubmissionService.submitCancellation(any())(any()))
        .thenReturn(Future.successful(Left(InvalidState)))

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, cancellationReasonRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(uaCaptor.capture)
      uaCaptor.getValue.get(CancellationReasonPage(departureId)).get mustBe validAnswer
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      checkCancellationStatus()

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, cancellationReasonRoute).withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      checkCancellationStatus()
      dataRetrievalNoData()

      val request = FakeRequest(GET, cancellationReasonRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      checkCancellationStatus()
      dataRetrievalNoData()

      val request = FakeRequest(POST, cancellationReasonRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
