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
import models.UserAnswers
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify
import play.api.test.FakeRequest
import play.api.test.Helpers._

class StartControllerSpec extends SpecBase {

  private lazy val startRoute: String = routes.StartController.start(departureId).url

  private val existingUserAnswers = emptyUserAnswers

  "Start Controller" - {

    "must redirect to confirm cancellation" - {
      "when there are no existing user answers" in {

        checkCancellationStatus()
        dataRetrievalNoData()

        val request = FakeRequest(GET, startRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ConfirmCancellationController.onPageLoad(departureId).url

        val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(uaCaptor.capture)
        uaCaptor.getValue.lastUpdated isAfter existingUserAnswers.lastUpdated mustBe true
      }

      "when there are existing user answers" in {

        checkCancellationStatus()
        dataRetrievalWithData(existingUserAnswers)

        val request = FakeRequest(GET, startRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.ConfirmCancellationController.onPageLoad(departureId).url

        val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(uaCaptor.capture)
        uaCaptor.getValue.lastUpdated equals existingUserAnswers.lastUpdated mustBe true
      }
    }
  }
}
