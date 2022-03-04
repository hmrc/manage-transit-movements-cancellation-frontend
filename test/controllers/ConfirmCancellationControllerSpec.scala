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

import base.{MockNunjucksRendererApp, SpecBase}
import forms.ConfirmCancellationFormProvider
import matchers.JsonMatchers
import models.{LocalReferenceNumber, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{times, verify, when}
import pages.ConfirmCancellationPage
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.Future

class ConfirmCancellationControllerSpec extends SpecBase with NunjucksSupport with MockNunjucksRendererApp with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new ConfirmCancellationFormProvider()
  private val form         = formProvider()
  private val template     = "confirmCancellation.njk"

  lazy val confirmCancellationRoute = routes.ConfirmCancellationController.onPageLoad(departureId).url

  "ConfirmCancellation Controller" - {

    "must return OK and the correct view for a GET" in {

      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(GET, confirmCancellationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"        -> form,
        "lrn"         -> LocalReferenceNumber(""),
        "departureId" -> departureId,
        "radios"      -> Radios.yesNo(form("value")),
        "onSubmitUrl" -> routes.ConfirmCancellationController.onSubmit(departureId).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = UserAnswers(departureId, eoriNumber).set(ConfirmCancellationPage(departureId), true).success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, confirmCancellationRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      templateCaptor.getValue mustEqual template

    }

    "must redirect to the next page when valid data is submitted and user selects Yes" in {

      checkCancellationStatus()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, confirmCancellationRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual s"${controllers.routes.CancellationReasonController.onPageLoad(departureId)}"

    }

    "must redirect to the next page when valid data is submitted and user selects No" in {

      checkCancellationStatus()
      when(mockSessionRepository.set(any())) thenReturn Future.successful(false)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, confirmCancellationRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual s"${frontendAppConfig.manageTransitMovementsViewDeparturesUrl}"

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(POST, confirmCancellationRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"        -> boundForm,
        "lrn"         -> LocalReferenceNumber(""),
        "departureId" -> departureId,
        "radios"      -> Radios.yesNo(boundForm("value")),
        "onSubmitUrl" -> routes.ConfirmCancellationController.onSubmit(departureId).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

  }
}
