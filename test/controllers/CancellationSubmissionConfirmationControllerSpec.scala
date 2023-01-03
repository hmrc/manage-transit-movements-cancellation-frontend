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
import models.DepartureStatus.DepartureSubmitted
import models.response.ResponseDeparture
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class CancellationSubmissionConfirmationControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute: Call = Call("GET", "/foo")

  private val mockDepartureResponse: ResponseDeparture =
    ResponseDeparture(
      lrn,
      DepartureSubmitted
    )

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

    "return OK and the correct view for a GET" in {

      when(mockConnector.getDeparture(any())(any()))
        .thenReturn(Future.successful(Some(mockDepartureResponse)))

      val request = FakeRequest(GET, routes.CancellationSubmissionConfirmationController.onPageLoad(departureId).url)

      val result = route(app, request).value

      status(result) mustEqual OK

    }

    "return Not_Found and the correct view for a GET when departure record is not found" in {

      when(mockConnector.getDeparture(any())(any()))
        .thenReturn(Future.successful(None))

      val request = FakeRequest(GET, routes.CancellationSubmissionConfirmationController.onPageLoad(departureId).url)

      val result = route(app, request).value

      status(result) mustEqual NOT_FOUND
    }
  }
}
