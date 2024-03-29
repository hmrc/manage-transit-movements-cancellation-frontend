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
import forms.ConfirmCancellationFormProvider
import pages.ConfirmCancellationPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConfirmCancellationView

class ConfirmCancellationControllerSpec extends SpecBase {

  private lazy val confirmCancellationRoute: String = routes.ConfirmCancellationController.onPageLoad(departureId, lrn).url
  private val form: Form[Boolean]                   = new ConfirmCancellationFormProvider()()

  "ConfirmCancellation Controller" - {

    "must redirect to CannotSendCancellationRequest page when cancellation status is not submittable on a GET" in {

      cancellationStatusNotSubmittable(departureId, lrn)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, confirmCancellationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.CannotSendCancellationRequestController.onPageLoad(departureId, lrn).url
    }

    "must return OK and the correct view for a GET" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, confirmCancellationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ConfirmCancellationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, departureId, lrn)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      cancellationStatusSubmittable(departureId, lrn)

      val answer      = true
      val userAnswers = emptyUserAnswers.setValue(ConfirmCancellationPage, answer)
      dataRetrievalWithData(userAnswers)

      val request = FakeRequest(GET, confirmCancellationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ConfirmCancellationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(answer), departureId, lrn)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted and user selects Yes" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, confirmCancellationRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.CancellationReasonController.onPageLoad(departureId, lrn).url
    }

    "must redirect to the next page when valid data is submitted and user selects No" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, confirmCancellationRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual "http://localhost:9485/manage-transit-movements/view-departure-declarations"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(POST, confirmCancellationRoute).withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalNoData()

      val request = FakeRequest(GET, confirmCancellationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      cancellationStatusSubmittable(departureId, lrn)

      dataRetrievalNoData()

      val request = FakeRequest(POST, confirmCancellationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

  }
}
