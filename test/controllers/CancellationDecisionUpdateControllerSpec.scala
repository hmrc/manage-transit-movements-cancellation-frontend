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
import generators.Generators
import models.messages.CancellationDecisionUpdate
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DepartureMessageService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewModels.CancellationDecisionUpdateViewModel
import viewModels.CancellationDecisionUpdateViewModel.CancellationDecisionUpdateViewModelProvider
import views.html.CancellationDecisionUpdateView

import scala.concurrent.Future

class CancellationDecisionUpdateControllerSpec extends SpecBase with Generators {

  private val mockDepartureMessageService = mock[DepartureMessageService]
  private val mockViewModelProvider       = mock[CancellationDecisionUpdateViewModelProvider]

  override def beforeEach(): Unit = {
    reset(mockDepartureMessageService); reset(mockViewModelProvider)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[DepartureMessageService].toInstance(mockDepartureMessageService))
      .overrides(bind[CancellationDecisionUpdateViewModelProvider].toInstance(mockViewModelProvider))

  "CancellationDecisionUpdate Controller" - {

    "return OK and the correct view for a GET" in {
      val message = arbitrary[CancellationDecisionUpdate].sample.value

      val rows = listWithMaxLength[SummaryListRow]().sample.value

      when(mockDepartureMessageService.cancellationDecisionUpdateMessage(any())(any(), any()))
        .thenReturn(Future.successful(Some(message)))

      when(mockViewModelProvider.apply(any())(any()))
        .thenReturn(CancellationDecisionUpdateViewModel(rows))

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, routes.CancellationDecisionUpdateController.onPageLoad(departureId).url)
      val view    = injector.instanceOf[CancellationDecisionUpdateView]
      val result  = route(app, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(message.outcome, rows, message.rejected)(request, messages).toString
    }

    "show technical difficulties page when no response from service" in {

      when(mockDepartureMessageService.cancellationDecisionUpdateMessage(any())(any(), any()))
        .thenReturn(Future.successful(None))

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET, routes.CancellationDecisionUpdateController.onPageLoad(departureId).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url
    }
  }
}
