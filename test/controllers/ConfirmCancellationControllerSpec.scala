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
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import pages.ConfirmCancellationPage
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class ConfirmCancellationControllerSpec extends SpecBase {

  private lazy val confirmCancellationRoute: String = routes.ConfirmCancellationController.onPageLoad(departureId).url

  "ConfirmCancellation Controller" - {

    "must return OK and the correct view for a GET" in {

      checkCancellationStatus()

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, confirmCancellationRoute)

      val result = route(app, request).value

      status(result) mustEqual OK
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      checkCancellationStatus()

      val userAnswers = emptyUserAnswers.setValue(ConfirmCancellationPage(departureId), true)
      dataRetrievalWithData(userAnswers)

      val request = FakeRequest(GET, confirmCancellationRoute)

      val result = route(app, request).value

      status(result) mustEqual OK
    }

    "must redirect to the next page when valid data is submitted and user selects Yes" in {

      checkCancellationStatus()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, confirmCancellationRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.CancellationReasonController.onPageLoad(departureId).url
    }

    "must redirect to the next page when valid data is submitted and user selects No" in {

      checkCancellationStatus()
      when(mockSessionRepository.set(any())) thenReturn Future.successful(false)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, confirmCancellationRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual s"${frontendAppConfig.manageTransitMovementsViewDeparturesUrl}"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      checkCancellationStatus()

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, confirmCancellationRoute).withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

  }
}
