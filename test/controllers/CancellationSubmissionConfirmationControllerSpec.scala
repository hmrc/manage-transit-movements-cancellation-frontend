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
import connectors.DepartureMovementConnector
import models.DepartureStatus.DepartureSubmitted
import models.LocalReferenceNumber
import models.response.ResponseDeparture
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class CancellationSubmissionConfirmationControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute: Call = Call("GET", "/foo")

  private val mockDepartureResponse: ResponseDeparture =
    ResponseDeparture(
      LocalReferenceNumber("lrn"),
      DepartureSubmitted
    )

  "CancellationSubmissionConfirmation Controller" - {

    "return OK and the correct view for a GET" in {

      val mockConnector = mock[DepartureMovementConnector]

      when(mockConnector.getDeparture(any())(any()))
        .thenReturn(Future.successful(Some(mockDepartureResponse)))

      val application = guiceApplicationBuilder().overrides(bind[DepartureMovementConnector].toInstance(mockConnector)).build()

      val request = FakeRequest(GET, routes.CancellationSubmissionConfirmationController.onPageLoad(departureId).url)

      val result = route(application, request).value

      status(result) mustEqual OK

      application.stop()

    }

    "return Not_Found and the correct view for a GET when departure record is not found" in {

      val mockConnector = mock[DepartureMovementConnector]

      when(mockConnector.getDeparture(any())(any()))
        .thenReturn(Future.successful(None))

      val application = guiceApplicationBuilder().overrides(bind[DepartureMovementConnector].toInstance(mockConnector)).build()

      val request = FakeRequest(GET, routes.CancellationSubmissionConfirmationController.onPageLoad(departureId).url)

      val result = route(application, request).value

      status(result) mustEqual NOT_FOUND

      application.stop()
    }
  }
}
