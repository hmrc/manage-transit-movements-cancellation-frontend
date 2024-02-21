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
import generated.CC015CType
import generators.Generators
import models.CustomsOffice
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ReferenceDataService
import viewModels.CannotSendCancellationRequestViewModel
import views.html.CannotSendCancellationRequestView

import scala.concurrent.Future

class CannotSendCancellationRequestControllerSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val mockReferenceDataService: ReferenceDataService = mock[ReferenceDataService]

  private val customsOffice =
    CustomsOffice("AB123", "", "GB", None)

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockReferenceDataService)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[ReferenceDataService].toInstance(mockReferenceDataService))

  "CannotSendUnloadingRemarksController" - {
    "return OK and the correct view for a GET" in {
      forAll(arbitrary[CC015CType]) {
        ie015 =>
          dataRetrievalWithData(emptyUserAnswers)

          val customsOfficeRefNumber = ie015.CustomsOfficeOfDeparture.referenceNumber

          val viewModel = CannotSendCancellationRequestViewModel(customsOffice)

          when(mockDepartureMessageService.getIE015(any())(any(), any())).thenReturn(Future.successful(Some(ie015)))
          when(mockReferenceDataService.getCustomsOfficeByCode(any())(any(), any())).thenReturn(Future.successful(customsOffice))

          val request = FakeRequest(GET, routes.CannotSendCancellationRequestController.onPageLoad(departureId, lrn).url)

          val result = route(app, request).value

          val view = app.injector.instanceOf[CannotSendCancellationRequestView]

          status(result) mustBe OK

          contentAsString(result) mustEqual
            view(lrn, departureId, viewModel)(request, messages).toString

          verify(mockReferenceDataService).getCustomsOfficeByCode(eqTo(customsOfficeRefNumber))(any(), any())
      }
    }

    "return technical difficulties when no IE015 found" in {
      dataRetrievalWithData(emptyUserAnswers)

      when(mockDepartureMessageService.getIE015(any())(any(), any())).thenReturn(Future.successful(None))

      val request = FakeRequest(GET, routes.CannotSendCancellationRequestController.onPageLoad(departureId, lrn).url)

      val result = route(app, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url
    }
  }
}
